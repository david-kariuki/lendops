package com.dk.lendops.loan.service.impl;

import com.dk.lendops.common.exception.BusinessException;
import com.dk.lendops.customer.entity.Customer;
import com.dk.lendops.customer.entity.CustomerLimit;
import com.dk.lendops.customer.repository.CustomerLimitRepository;
import com.dk.lendops.customer.repository.CustomerRepository;
import com.dk.lendops.loan.dto.request.CreateLoanRequest;
import com.dk.lendops.loan.dto.response.LoanInstallmentResponse;
import com.dk.lendops.loan.dto.response.LoanResponse;
import com.dk.lendops.loan.entity.Loan;
import com.dk.lendops.loan.entity.LoanInstallment;
import com.dk.lendops.loan.enums.InstallmentStatus;
import com.dk.lendops.loan.enums.LoanStatus;
import com.dk.lendops.loan.enums.LoanStructureType;
import com.dk.lendops.loan.repository.LoanInstallmentRepository;
import com.dk.lendops.loan.repository.LoanRepository;
import com.dk.lendops.loan.service.LoanService;
import com.dk.lendops.product.dto.request.config.*;
import com.dk.lendops.product.entity.Product;
import com.dk.lendops.product.entity.ProductConfig;
import com.dk.lendops.product.enums.ConfigType;
import com.dk.lendops.product.enums.TenureUnit;
import com.dk.lendops.product.repository.ProductConfigRepository;
import com.dk.lendops.product.repository.ProductRepository;
import com.dk.lendops.product.service.impl.ProductConfigMapper;
import jakarta.servlet.Servlet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Loan service
 *
 * @author David Kariuki
 * @see LoanService Service interface
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    private final CustomerRepository customerRepository;
    private final CustomerLimitRepository customerLimitRepository;

    private final ProductRepository productRepository;
    private final ProductConfigRepository productConfigRepository;
    private final ProductConfigMapper productConfigMapper;
    private final Servlet servlet;

    /**
     * Creates loan
     *
     * @param headers Headers
     * @param request Loan request
     * @return Loan response
     */
    @Override
    @Transactional
    public LoanResponse createLoan(Map<String, String> headers, CreateLoanRequest request) {

        Customer customer = customerRepository.findByCustomerRef(request.getCustomerRef())
                .orElseThrow(() -> new BusinessException(
                        404,
                        "Customer not found!",
                        "Customer not found with ref : " + request.getCustomerRef()));

        CustomerLimit activeLimit = customerLimitRepository
                .findByCustomerIdAndActiveTrue(customer.getId())
                .orElseThrow(() -> new BusinessException(
                        404,
                        "Customer limit not found!",
                        "No active limit found for customer with ref : " + request.getCustomerRef()));

        // Check if amount requested surpasses customer limit
        if (request.getPrincipalAmount().compareTo(activeLimit.getLimitAmount()) > 0) {
            throw new BusinessException(
                    400,
                    "Requested amount exceeds limit!",
                    "Requested amount " + request.getPrincipalAmount() +
                            " exceeds limit " + activeLimit.getLimitAmount());
        }

        Product product = productRepository.findByCode(request.getProductCode())
                .orElseThrow(() -> new BusinessException(
                        404,
                        "Product not found!",
                        "Product with code " + request.getProductCode() + " not found"));

        // Get all product configs for the product
        Map<ConfigType, Object> configMap = loadProductConfigs(product.getId());

        // Get the different product configurations
        TenureConfig tenureConfig = (TenureConfig) configMap.get(ConfigType.TENURE);
        BillingConfig billingConfig = (BillingConfig) configMap.get(ConfigType.BILLING);
        LoanStructureConfig loanStructureConfig = (LoanStructureConfig) configMap.get(ConfigType.LOAN_STRUCTURE);
        FeesConfig feesConfig = (FeesConfig) configMap.get(ConfigType.FEES);

        if (tenureConfig == null || billingConfig == null || loanStructureConfig == null) {
            throw new BusinessException(
                    400,
                    "Product configuration incomplete!",
                    "Missing required product configuration for product with code " + request.getProductCode());
        }

        LocalDateTime disbursedAt = LocalDateTime.now();
        LocalDateTime dueDate = deriveDueDate(disbursedAt, tenureConfig);

        BigDecimal totalRepayableAmount = calculateTotalRepayableAmount(request.getPrincipalAmount(), feesConfig);

        Loan loan = Loan.builder()
                .loanRef(generateLoanRef())
                .customer(customer)
                .product(product)
                .principalAmount(request.getPrincipalAmount())
                .totalRepayableAmount(totalRepayableAmount)
                .disbursedAt(disbursedAt)
                .dueDate(dueDate)
                .billingType(billingConfig.getBillingType())
                .loanStructureType(loanStructureConfig.getStructureType())
                .status(LoanStatus.OPEN)
                .amountPaid(BigDecimal.ZERO)
                .outstandingAmount(totalRepayableAmount)
                .build();

        Loan savedLoan = loanRepository.save(loan);

        List<LoanInstallment> installments = createInstallments(savedLoan, loanStructureConfig, tenureConfig);

        if (!installments.isEmpty()) {
            log.debug("Saving installments {}", savedLoan.getLoanRef());
            loanInstallmentRepository.saveAll(installments); // Save all installments
        }

        log.debug("Created loan {}", savedLoan.getLoanRef());

        return mapResponse(savedLoan, installments);
    }

    /**
     * Gets loan by reference
     *
     * @param headers Headers
     * @param loanRef Loan reference
     * @return Loan response
     */
    @Override
    @Transactional
    public LoanResponse getLoanByRef(Map<String, String> headers, String loanRef) {

        Loan loan = loanRepository.findByLoanRef(loanRef)
                .orElseThrow(() -> new BusinessException(
                        404,
                        "Loan not found!",
                        "Loan with ref " + loanRef + " not found"));

        List<LoanInstallment> installments = loanInstallmentRepository.findByLoanId(loan.getId());

        return mapResponse(loan, installments);
    }

    /**
     * Loads product configs for a given product
     *
     * @param productId Product id
     * @return Map of config type to config value
     */
    private Map<ConfigType, Object> loadProductConfigs(Long productId) {

        List<ProductConfig> configs = productConfigRepository.findByProductIdAndActiveTrue(productId);

        Map<ConfigType, Object> configMap = new EnumMap<>(ConfigType.class);
        for (ProductConfig productConfig : configs) {

            var typedConfig = switch (productConfig.getConfigType()) {
                case TENURE -> productConfigMapper.fromJson(productConfig.getConfigJson(), TenureConfig.class);
                case BILLING -> productConfigMapper.fromJson(productConfig.getConfigJson(), BillingConfig.class);
                case LOAN_STRUCTURE ->
                        productConfigMapper.fromJson(productConfig.getConfigJson(), LoanStructureConfig.class);
                case FEES -> productConfigMapper.fromJson(productConfig.getConfigJson(), FeesConfig.class);
            };

            configMap.put(productConfig.getConfigType(), typedConfig);
        }

        return configMap;
    }

    /**
     * Generates a unique loan reference
     *
     * @return Loan reference
     */
    private String generateLoanRef() {
        return "LN-".concat(UUID.randomUUID().toString().substring(0, 8));
    }

    /**
     * Calculates total repayable amount by applying fees to the principal amount, based on the fees' configuration.
     *
     * @param principalAmount Principal amount
     * @param feesConfig      Fees configuration
     * @return Total repayable amount
     */
    private BigDecimal calculateTotalRepayableAmount(final BigDecimal principalAmount, final FeesConfig feesConfig) {

        if (feesConfig == null || feesConfig.getServiceFee() == null) {
            return principalAmount; // No fees configured, so total repayable amount is just the principal
        }

        FeeDetailConfig serviceFee = feesConfig.getServiceFee();

        // Service fee only applies at loan creation in this implementation, so I only consider the upfront fee type
        if (!Boolean.TRUE.equals(serviceFee.getEnabled())) {
            return principalAmount; // Service fee not enabled
        }

        if (serviceFee.getValue() == null) {
            throw new BusinessException(400, "Service fee value is required when enabled");
        }

        // Calculate the fee amount based on the calculation type (fixed or percentage)
        BigDecimal feeAmount = switch (serviceFee.getCalculationType()) {
            case "FIXED" -> BigDecimal.valueOf(serviceFee.getValue());
            case "PERCENTAGE" -> principalAmount
                    .multiply(BigDecimal.valueOf(serviceFee.getValue()))
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            default -> throw new BusinessException(
                    400,
                    "Unsupported service fee calculation type");
        };

        return principalAmount.add(feeAmount); // original principal plus the service fee charged upfront
    }

    /**
     * Creates loan installments based on the loan structure configuration and tenure
     *
     * @param loan                Loan for which installments are to be created
     * @param loanStructureConfig Loan structure configuration
     * @param tenureConfig        Tenure configuration
     * @return List of loan installments
     */
    private List<LoanInstallment> createInstallments(
            final Loan loan, final LoanStructureConfig loanStructureConfig, final TenureConfig tenureConfig) {

        List<LoanInstallment> installments = new ArrayList<>();

        // Only create installments when product is configured as installment-based
        if (loanStructureConfig.getStructureType() != LoanStructureType.INSTALLMENT) {
            return installments; // Return empty list
        }


        int numberOfInstallments = tenureConfig.getDefaultValue();

        /*
         * Calculate the principal installment amount by dividing the principal by the number of installments.
         * Using scale of 2 for cents and rounding half up to ensure that money is not lost due to rounding, when
         * the amount cannot be divided evenly
         */
        BigDecimal principalInstallmentAmount = loan.getPrincipalAmount()
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, BigDecimal.ROUND_HALF_UP);

        // Calculate the total installment amount by dividing the total repayable amount by the number of installments
        BigDecimal totalInstallmentAmount = loan.getTotalRepayableAmount()
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, BigDecimal.ROUND_HALF_UP);

        // Keep track of the total principal assigned to installments so far, to handle any rounding differences in the last installment
        BigDecimal totalPrincipalAssigned = BigDecimal.ZERO;

        // Keep track of the total amount assigned to installments so far, to handle any rounding differences in the last installment
        BigDecimal totalAmountAssigned = BigDecimal.ZERO;

        // Create one installment per period and move the due date forward each time
        for (int i = 1; i <= numberOfInstallments; i++) {

            BigDecimal principalAmount = principalInstallmentAmount;
            BigDecimal totalAmount = totalInstallmentAmount;

            /*
             * Due to the rounding, the equal splits may not add up exactly to the original amount.
             * So the last installment,takes what is left. This keeps the final sum equal to the original loan amounts.
             */
            if (i == numberOfInstallments) {
                principalAmount = loan.getPrincipalAmount().subtract(totalPrincipalAssigned);
                totalAmount = loan.getTotalRepayableAmount().subtract(totalAmountAssigned);
            }

            LocalDateTime installmentDueDate = deriveInstallmentDueDate(loan.getDisbursedAt(), i, tenureConfig.getUnit());

            LoanInstallment installment = LoanInstallment.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .dueDate(installmentDueDate)
                    .principalAmount(principalAmount)
                    .totalAmount(totalAmount)
                    .status(InstallmentStatus.PENDING)
                    .amountPaid(BigDecimal.ZERO)
                    .outstandingAmount(totalAmount)
                    .build();

            // Update the running totals before moving to the next installment
            totalPrincipalAssigned = totalPrincipalAssigned.add(principalAmount);
            totalAmountAssigned = totalAmountAssigned.add(totalAmount);

            installments.add(installment);
        }

        return installments;
    }

    /**
     * Maps loan and its installments to loan response
     *
     * @param loan             Loan
     * @param loanInstallments List of loan installments
     * @return Loan response
     */
    private LoanResponse mapResponse(final Loan loan, final List<LoanInstallment> loanInstallments) {

        List<LoanInstallmentResponse> installmentResponses = loanInstallments.stream()
                .map(installment -> LoanInstallmentResponse.builder()
                        .installmentNumber(installment.getInstallmentNumber())
                        .dueDate(installment.getDueDate())
                        .principalAmount(installment.getPrincipalAmount())
                        .totalAmount(installment.getTotalAmount())
                        .status(installment.getStatus().name())
                        .build())
                .toList();

        return LoanResponse.builder()
                .id(loan.getId())
                .loanRef(loan.getLoanRef())
                .customerRef(loan.getCustomer().getCustomerRef())
                .productCode(loan.getProduct().getCode())
                .principalAmount(loan.getPrincipalAmount())
                .totalRepayableAmount(loan.getTotalRepayableAmount())
                .amountPaid(loan.getAmountPaid())
                .outstandingAmount(loan.getOutstandingAmount())
                .disbursedAt(loan.getDisbursedAt())
                .dueDate(loan.getDueDate())
                .billingType(loan.getBillingType())
                .loanStructureType(loan.getLoanStructureType())
                .status(loan.getStatus().name())
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .installments(installmentResponses)
                .build();
    }

    /**
     * Derives loan due date based on disbursement date and tenure configuration.
     * The due date is calculated by adding the tenure duration to the disbursement date.
     * The tenure duration is determined by the default value and unit specified in the tenure configuration.
     *
     * @param disbursedAt  Loan disbursement date
     * @param tenureConfig Tenure configuration
     * @return Due date
     */
    private LocalDateTime deriveDueDate(final LocalDateTime disbursedAt, final TenureConfig tenureConfig) {
        return switch (tenureConfig.getUnit()) {
            case DAYS -> disbursedAt.plusDays(tenureConfig.getDefaultValue());
            case WEEKS -> disbursedAt.plusWeeks(tenureConfig.getDefaultValue());
            case MONTHS -> disbursedAt.plusMonths(tenureConfig.getDefaultValue());
        };
    }

    /**
     * Derives installment due date based on tenure configuration and installment number
     *
     * @param disbursedAt       Loan disbursement date
     * @param installmentNumber Installment number (1 for first installment, 2 for second, etc.)
     * @param tenureUnit        Tenure unit (days, weeks, months)
     * @return Due date
     */
    private LocalDateTime deriveInstallmentDueDate(
            final LocalDateTime disbursedAt, final int installmentNumber, final TenureUnit tenureUnit) {
        return switch (tenureUnit) {
            case DAYS -> disbursedAt.plusDays(installmentNumber);
            case WEEKS -> disbursedAt.plusWeeks(installmentNumber);
            case MONTHS -> disbursedAt.plusMonths(installmentNumber);
        };
    }
}
