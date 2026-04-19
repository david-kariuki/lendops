package com.dk.lendops.product.dto.request.config;

import com.dk.lendops.loan.enums.LoanStructureType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Loan structure config
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanStructureConfig {

    @NotNull(message = "Loan structure type is required")
    private LoanStructureType structureType;
}