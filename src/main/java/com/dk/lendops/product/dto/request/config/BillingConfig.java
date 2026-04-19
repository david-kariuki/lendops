package com.dk.lendops.product.dto.request.config;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Billing type is required")
    private String billingType;
}