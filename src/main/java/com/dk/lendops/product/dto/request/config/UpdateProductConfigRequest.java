package com.dk.lendops.product.dto.request.config;


import com.dk.lendops.product.enums.ConfigType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request to update product config
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateProductConfigRequest {

    @NotNull(message = "Config type is required")
    private ConfigType configType;

    @NotNull(message = "Config payload is required")
    private Object config;
}
