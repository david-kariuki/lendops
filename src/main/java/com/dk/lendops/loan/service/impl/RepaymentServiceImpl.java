package com.dk.lendops.loan.service.impl;

import com.dk.lendops.common.exception.BusinessException;
import com.dk.lendops.loan.dto.request.repayment.CreateRepaymentRequest;
import com.dk.lendops.loan.dto.response.LoanInstallmentResponse;
import com.dk.lendops.loan.dto.response.repayment.RepaymentResponse;
import com.dk.lendops.loan.entity.Loan;
import com.dk.lendops.loan.entity.LoanInstallment;
import com.dk.lendops.loan.entity.Repayment;
import com.dk.lendops.loan.enums.InstallmentStatus;
import com.dk.lendops.loan.enums.LoanStatus;
import com.dk.lendops.loan.enums.RepaymentStatus;
import com.dk.lendops.loan.repository.LoanInstallmentRepository;
import com.dk.lendops.loan.repository.LoanRepository;
import com.dk.lendops.loan.repository.RepaymentRepository;
import com.dk.lendops.loan.service.RepaymentService;
import com.dk.lendops.notification.enums.NotificationType;
import com.dk.lendops.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repayment service
 *
 * @author David Kariuki
 * @see RepaymentService Service interface
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepaymentServiceImpl implements RepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final NotificationService notificationService;

    /**
     * Creates repayment
     *
     * @param headers Headers
     * @param request Repayment request
     * @return Repayment response
     */
    @Override
    @Transactional
    public RepaymentResponse createRepayment(Map<String, String> headers, CreateRepaymentRequest request) {

        // Prevent the same payment from being recorded twice
        if (repaymentRepository.existsByPaymentReference(request.getPaymentReference())) {
            throw new BusinessException(
                    409,
                    "Payment reference already exists!",
                    "Repayment already exists with payment reference " + request.getPaymentReference());
        }

        Loan loan = loanRepository.findByLoanRef(request.getLoanRef())
                .orElseThrow(() -> new BusinessException(
                        404,
                        "Loan not found!",
                        "Loan with ref " + request.getLoanRef() + " not found"));

        // No repayment should be accepted once the loan is already closed
        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new BusinessException(
                    409,
                    "Loan is already closed!",
                    "Loan with ref " + request.getLoanRef() + " is already closed");
        }

        // Reject overpayments
        if (request.getAmountPaid().compareTo(loan.getOutstandingAmount()) > 0) {
            throw new BusinessException(
                    400,
                    "Amount paid exceeds outstanding amount!",
                    "Amount paid " + request.getAmountPaid() +
                            " exceeds outstanding amount " + loan.getOutstandingAmount());
        }

        // Amount to be used to distribute across installments
        BigDecimal remainingAmount = request.getAmountPaid();

        // Apply repayment from the earliest installment to the latest, until the repayment amount is finished
        List<LoanInstallment> installments = loanInstallmentRepository.findByLoanId(loan.getId()).stream()
                .sorted(Comparator.comparing(LoanInstallment::getInstallmentNumber))
                .toList();

        for (LoanInstallment installment : installments) {

            // If the remaining amount is zero or negative, we have applied the entire repayment amount and can stop
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            // Skip installments that are already fully settled
            if (installment.getOutstandingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // Apply only what is needed for this installment, or what is left from the repayment
            BigDecimal amountToApply = remainingAmount.min(installment.getOutstandingAmount());

            // Increase the amount already paid on this installment
            installment.setAmountPaid(installment.getAmountPaid().add(amountToApply));

            // Reduce the remaining balance on this installment
            installment.setOutstandingAmount(installment.getOutstandingAmount().subtract(amountToApply));

            // Once nothing is left outstanding, the installment is fully paid
            if (installment.getOutstandingAmount().compareTo(BigDecimal.ZERO) == 0) {
                installment.setStatus(InstallmentStatus.PAID);
            }

            loanInstallmentRepository.save(installment);

            // Reduce the repayment amount left to allocate to later installments
            remainingAmount = remainingAmount.subtract(amountToApply);
        }

        // Reflect the repayment on the loan level by reducing the outstanding amount and increasing the amount paid
        loan.setAmountPaid(loan.getAmountPaid().add(request.getAmountPaid()));
        loan.setOutstandingAmount(loan.getOutstandingAmount().subtract(request.getAmountPaid()));

        // Close the loan once the full amount has been repaid
        if (loan.getOutstandingAmount().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.CLOSED);
        }

        loanRepository.save(loan);

        // Save the repayment transaction for history and audit
        Repayment repayment = Repayment.builder()
                .repaymentRef(generateRepaymentRef())
                .loan(loan)
                .amountPaid(request.getAmountPaid())
                .paymentReference(request.getPaymentReference())
                .paidAt(LocalDateTime.now())
                .status(RepaymentStatus.SUCCESS)
                .build();

        Repayment savedRepayment = repaymentRepository.save(repayment);

        log.debug("Created repayment {} for loan {}", savedRepayment.getRepaymentRef(), loan.getLoanRef());

        // Get the updated installments to return in the response
        List<LoanInstallmentResponse> installmentResponses = loanInstallmentRepository.findByLoanId(loan.getId())
                .stream()
                .sorted(Comparator.comparing(LoanInstallment::getInstallmentNumber))
                .map(installment -> LoanInstallmentResponse.builder()
                        .installmentNumber(installment.getInstallmentNumber())
                        .dueDate(installment.getDueDate())
                        .principalAmount(installment.getPrincipalAmount())
                        .totalAmount(installment.getTotalAmount())
                        .status(installment.getStatus().name())
                        .build())
                .toList();

        // Send notification
        notificationService.createNotification(
                loan.getCustomer().getCustomerRef(),
                loan.getLoanRef(),
                loan.getCustomer().getEmailAddress(),
                "Repayment of " + savedRepayment.getAmountPaid() + " has been received for loan " + loan.getLoanRef() + ".",
                NotificationType.REPAYMENT_RECEIVED);

        return RepaymentResponse.builder()
                .repaymentRef(savedRepayment.getRepaymentRef())
                .loanRef(loan.getLoanRef())
                .amountPaid(savedRepayment.getAmountPaid())
                .paymentReference(savedRepayment.getPaymentReference())
                .paidAt(savedRepayment.getPaidAt())
                .status(savedRepayment.getStatus().name())
                .loanStatus(loan.getStatus().name())
                .loanOutstandingAmount(loan.getOutstandingAmount())
                .installments(installmentResponses)
                .build();
    }

    /**
     * Generates repayment reference
     *
     * @return Repayment reference
     */
    private String generateRepaymentRef() {
        return "RP-".concat(UUID.randomUUID().toString().substring(0, 8));
    }
}
