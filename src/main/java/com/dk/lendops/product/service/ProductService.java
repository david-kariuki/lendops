package com.dk.lendops.product.service;

import com.dk.lendops.product.dto.request.CreateProductRequest;
import com.dk.lendops.product.dto.request.config.UpdateProductConfigRequest;
import com.dk.lendops.product.dto.response.ProductResponse;
import com.dk.lendops.product.service.impl.ProductServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * Product service
 *
 * @author David Kariuki
 * @see ProductServiceImpl Product Service Implementation
 */
public interface ProductService {

    /**
     * Creates a new product with its configurations
     *
     * @param headers headers
     * @param request Product creation request
     * @return ProductResponse
     */
    ProductResponse createProduct(final Map<String, String> headers, final CreateProductRequest request);

    /**
     * Gets a product by its code
     *
     * @param code Product code
     * @return ProductResponse
     */
    ProductResponse getProductByCode(final Map<String, String> headers, final String code);

    /**
     * Get all products
     *
     * @param headers Headers
     * @return A list of all products
     */
    List<ProductResponse> getAllProducts(final Map<String, String> headers);

    /**
     * Updates product config by creating a new active version
     *
     * @param headers Headers
     * @param code    Product code
     * @param request Config update request
     * @return ProductResponse
     */
    ProductResponse updateProductConfig(
            final Map<String, String> headers, final String code, final UpdateProductConfigRequest request);
}
