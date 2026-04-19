package com.dk.lendops.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Request to create customer
 *
 * @author David Kariuki
 */
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCustomerRequest extends BaseCustomerRequest {

    @NotBlank(message = "Customer reference is required")
    private String customerRef;

    @NotNull(message = "Loan limit is required")
    @Positive(message = "Loan limit must be greater than zero")
    private BigDecimal loanLimit;
}