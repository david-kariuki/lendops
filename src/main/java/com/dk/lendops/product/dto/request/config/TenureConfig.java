package com.dk.lendops.product.dto.request.config;

import com.dk.lendops.product.enums.TenureUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Tenure config
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenureConfig {

    @NotNull(message = "Minimum tenure value is required")
    @Positive(message = "Minimum tenure value must be greater than zero")
    private Integer minimumValue;

    @NotNull(message = "Maximum tenure value is required")
    @Positive(message = "Maximum tenure value must be greater than zero")
    private Integer maximumValue;

    @NotNull(message = "Default tenure value is required")
    @Positive(message = "Default tenure value must be greater than zero")
    private Integer defaultValue;

    @NotNull(message = "Tenure unit is required")
    private TenureUnit unit;
}