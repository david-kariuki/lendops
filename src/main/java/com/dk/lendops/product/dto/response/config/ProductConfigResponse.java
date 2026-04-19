package com.dk.lendops.product.dto.response.config;

import com.dk.lendops.product.enums.ConfigType;
import lombok.*;

/**
 * Product config response
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductConfigResponse {

    private ConfigType configType;
    private Object config;
}