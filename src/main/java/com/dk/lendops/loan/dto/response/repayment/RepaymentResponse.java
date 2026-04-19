package com.dk.lendops.loan.dto.response.repayment;

import com.dk.lendops.loan.dto.response.LoanInstallmentResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repayment response
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepaymentResponse {

    private String repaymentRef;
    private String loanRef;
    private BigDecimal amountPaid;
    private String paymentReference;
    private LocalDateTime paidAt;
    private String status;
    private String loanStatus;
    private BigDecimal loanOutstandingAmount;
    private List<LoanInstallmentResponse> installments;
}
