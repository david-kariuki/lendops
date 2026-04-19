package com.dk.lendops.loan.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Loan installment response
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanInstallmentResponse {

    private Integer installmentNumber;
    private LocalDateTime dueDate;
    private BigDecimal principalAmount;
    private BigDecimal totalAmount;
    private String status;
}
