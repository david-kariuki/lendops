package com.dk.lendops.loan.dto.response;

import com.dk.lendops.loan.enums.LoanStructureType;
import com.dk.lendops.product.enums.BillingType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Loan response
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanResponse {

    private Long id;
    private String loanRef;
    private String customerRef;
    private String productCode;
    private BigDecimal principalAmount;
    private BigDecimal totalRepayableAmount;
    private BigDecimal amountPaid;
    private BigDecimal outstandingAmount;
    private LocalDateTime disbursedAt;
    private LocalDateTime dueDate;
    private BillingType billingType;
    private LoanStructureType loanStructureType;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LoanInstallmentResponse> installments;
}
