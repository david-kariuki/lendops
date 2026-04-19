package com.dk.lendops.customer.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

/**
 * Request to update customer limit
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateCustomerLimitRequest {

    @NotNull(message = "Loan limit is required")
    @Positive(message = "Loan limit must be greater than zero")
    private BigDecimal loanLimit;
}
