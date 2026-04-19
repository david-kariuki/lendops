package com.dk.lendops.product.dto.request.config;

import jakarta.validation.Valid;
import lombok.*;

/**
 * Fees config
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeesConfig {

    @Valid
    private FeeDetailConfig serviceFee;

    @Valid
    private FeeDetailConfig dailyFee;

    @Valid
    private FeeDetailConfig lateFee;
}