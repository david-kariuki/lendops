package com.dk.lendops.product.dto.request.config;

import com.dk.lendops.product.enums.BillingType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Billing config
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillingConfig {

    @NotNull(message = "Billing type is required!")
    private BillingType billingType;
}