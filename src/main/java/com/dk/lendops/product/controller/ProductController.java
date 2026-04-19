package com.dk.lendops.product.controller;

import com.dk.lendops.common.response.ApiResponse;
import com.dk.lendops.common.response.ApiResponseBuilder;
import com.dk.lendops.product.dto.request.CreateProductRequest;
import com.dk.lendops.product.dto.request.config.UpdateProductConfigRequest;
import com.dk.lendops.product.dto.response.ProductResponse;
import com.dk.lendops.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Product controller
 *
 * @author David Kariuki
 * @see ProductService Product service
 */
@Tag(name = "Product APIs")
@RestController
@RequestMapping(
        value = ProductController.BASE_PATH,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProductController {

    public static final String BASE_PATH = "/api/v1/products";
    private final ProductService productService;
    private final ApiResponseBuilder apiResponseBuilder;

    @Operation(
            summary = "Create product",
            description = "Creates a new loan product")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProductResponse> createProduct(
            @RequestHeader Map<String, String> headers, @Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(headers, request);
        return apiResponseBuilder.success(response);
    }

    @Operation(
            summary = "Get product",
            description = "Fetch product with configs")
    @GetMapping("/{code}")
    public ApiResponse<ProductResponse> getProduct(
            @RequestHeader Map<String, String> headers, @PathVariable String code) {
        ProductResponse response = productService.getProductByCode(headers, code);
        return apiResponseBuilder.success(response);
    }

    @Operation(
            summary = "Get all products",
            description = "Fetches all products with active configs")
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts(@RequestHeader Map<String, String> headers) {
        List<ProductResponse> response = productService.getAllProducts(headers);
        return apiResponseBuilder.success(response);
    }

    @Operation(
            summary = "Update product config",
            description = "Updates a product config by creating a new version")
    @PutMapping(value = "/{code}/configs", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProductResponse> updateProductConfig(
            @RequestHeader Map<String, String> headers,
            @PathVariable String code,
            @Valid @RequestBody UpdateProductConfigRequest request) {
        ProductResponse response = productService.updateProductConfig(headers, code, request);
        return apiResponseBuilder.success(response);
    }
}
