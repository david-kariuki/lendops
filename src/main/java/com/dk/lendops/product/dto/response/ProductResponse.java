package com.dk.lendops.product.dto.response;

import com.dk.lendops.product.dto.response.config.ProductConfigResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ProductResponse
 *
 * @author David Kariuki
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ProductConfigResponse> configs;
}
