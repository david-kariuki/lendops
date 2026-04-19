package com.dk.lendops.loan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

/**
 * Request to create loan
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateLoanRequest {

    @NotBlank(message = "Customer reference is required")
    private String customerRef;

    @NotBlank(message = "Product code is required")
    private String productCode;

    @NotNull(message = "Principal amount is required")
    @Positive(message = "Principal amount must be greater than zero")
    private BigDecimal principalAmount;
}
