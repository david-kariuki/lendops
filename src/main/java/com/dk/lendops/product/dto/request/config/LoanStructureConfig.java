package com.dk.lendops.product.dto.request.config;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Loan structure type is required")
    private String structureType;
}