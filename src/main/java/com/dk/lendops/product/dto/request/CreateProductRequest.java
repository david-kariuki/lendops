package com.dk.lendops.product.dto.request;

import com.dk.lendops.product.dto.request.config.ProductConfigRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

/**
 * Request to create a product
 *
 * @author David Kariuki
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Product code is required")
    private String code;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotBlank(message = "Product status is required")
    private String status;

    @Valid
    private List<ProductConfigRequest> configs;
}