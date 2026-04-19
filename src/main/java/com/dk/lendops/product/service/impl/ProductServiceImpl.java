package com.dk.lendops.product.service.impl;

import com.dk.lendops.product.dto.request.CreateProductRequest;
import com.dk.lendops.product.dto.request.config.*;
import com.dk.lendops.product.dto.response.ProductResponse;
import com.dk.lendops.product.dto.response.config.ProductConfigResponse;
import com.dk.lendops.product.entity.Product;
import com.dk.lendops.product.entity.ProductConfig;
import com.dk.lendops.product.enums.ConfigType;
import com.dk.lendops.product.enums.ProductStatus;
import com.dk.lendops.product.repository.ProductConfigRepository;
import com.dk.lendops.product.repository.ProductRepository;
import com.dk.lendops.product.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Product service
 *
 * @author David Kariuki
 * @see ProductService Product service implementation interface
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductConfigRepository productConfigRepository;
    private final ProductConfigMapper productConfigMapper;
    private final Validator validator;

    @Override
    @Transactional
    public ProductResponse createProduct(final Map<String, String> headers, final CreateProductRequest request) {

        // Prevent duplicate product creation by code
        if (productRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Product with code " + request.getCode() + " already exists");
        }

        Product product = Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .status(ProductStatus.valueOf(request.getStatus()))
                .build();

        Product savedProduct = productRepository.save(product);

        if (request.getConfigs() != null && !request.getConfigs().isEmpty()) {

            // Used to detect duplicate config types in request
            Set<ConfigType> seenConfigTypes = new HashSet<>();

            for (var configRequest : request.getConfigs()) {

                // Ensure each config type appears only once per request
                if (!seenConfigTypes.add(configRequest.getConfigType())) {
                    throw new IllegalArgumentException("Duplicate config type: " + configRequest.getConfigType());
                }

                // Convert incoming raw payload into a strongly typed config
                Object typedConfig = mapTypedConfig(configRequest.getConfigType(), configRequest.getConfig());

                // Validate both structure (jakarta) and business rules
                validateTypedConfig(typedConfig);

                ProductConfig productConfig = ProductConfig.builder()
                        .product(savedProduct)
                        .configType(configRequest.getConfigType())
                        .configVersion(1) // initial version
                        .active(true) // always active on creation
                        .configJson(productConfigMapper.toJson(typedConfig))
                        .build();

                productConfigRepository.save(productConfig);
                log.debug("Saved product config for type {}", configRequest.getConfigType());
            }
        }

        log.debug("Saved product {}", savedProduct.getCode());

        // Return full product including configs for consistency
        return getProductByCode(headers, savedProduct.getCode());
    }

    @Override
    public ProductResponse getProductByCode(final Map<String, String> headers, final String code) {

        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        var configResponses = productConfigRepository.findByProductIdAndActiveTrue(product.getId()).stream()
                // Ensure consistent ordering in API response
                .sorted(Comparator.comparing(ProductConfig::getConfigType))
                .map(config -> ProductConfigResponse.builder()
                        .configType(config.getConfigType())
                        // Convert stored JSON back to typed config
                        .config(mapStoredConfig(config))
                        .build())
                .toList();

        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .status(product.getStatus().name())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .configs(configResponses)
                .build();
    }

    /**
     * Get all products
     *
     * @param headers Headers
     * @return A list of all products
     */
    @Override
    public List<ProductResponse> getAllProducts(Map<String, String> headers) {

        // Fetch all products
        var products = productRepository.findAll();

        return products.stream()
                .sorted(Comparator.comparing(Product::getCode))
                .map(product -> {

                    // Fetch only active configs for each product
                    var configs = productConfigRepository
                            .findByProductIdAndActiveTrue(product.getId()).stream()
                            .sorted(Comparator.comparing(ProductConfig::getConfigType)) // predictable config order
                            .map(config -> ProductConfigResponse.builder()
                                    .configType(config.getConfigType())
                                    // Convert stored JSON back to typed config
                                    .config(mapStoredConfig(config))
                                    .build())
                            .toList();

                    // Build product response with configs
                    return ProductResponse.builder()
                            .id(product.getId())
                            .code(product.getCode())
                            .name(product.getName())
                            .description(product.getDescription())
                            .status(product.getStatus().name())
                            .createdAt(product.getCreatedAt())
                            .updatedAt(product.getUpdatedAt())
                            .configs(configs)
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse updateProductConfig(
            final Map<String, String> headers, final String code, final UpdateProductConfigRequest request) {

        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Convert and validate incoming config
        Object typedConfig = mapTypedConfig(request.getConfigType(), request.getConfig());
        validateTypedConfig(typedConfig);

        // Deactivate currently active config (if exists)
        productConfigRepository.findByProductIdAndConfigTypeAndActiveTrue(product.getId(), request.getConfigType())
                .ifPresent(activeConfig -> {
                    activeConfig.setActive(false);
                    productConfigRepository.save(activeConfig);
                    log.debug("Deactivated config type {} version {} for product {}",
                            activeConfig.getConfigType(), activeConfig.getConfigVersion(), product.getCode());
                });

        // Compute next version number
        int nextVersion = productConfigRepository
                .findTopByProductIdAndConfigTypeOrderByConfigVersionDesc(product.getId(), request.getConfigType())
                .map(ProductConfig::getConfigVersion)
                .map(version -> version + 1)
                .orElse(1);

        // Persist new active version
        ProductConfig newConfig = ProductConfig.builder()
                .product(product)
                .configType(request.getConfigType())
                .configVersion(nextVersion)
                .active(true)
                .configJson(productConfigMapper.toJson(typedConfig))
                .build();

        productConfigRepository.save(newConfig);

        log.debug("Saved config type {} version {} for product {}",
                request.getConfigType(), nextVersion, product.getCode());

        return getProductByCode(headers, product.getCode());
    }

    /**
     * Maps incoming config payload to typed config
     *
     * @param configType Config type
     * @param config     Config
     * @return Mapped config
     */
    private Object mapTypedConfig(final ConfigType configType, final Object config) {
        return switch (configType) {
            case TENURE -> productConfigMapper.convertValue(config, TenureConfig.class);
            case FEES -> productConfigMapper.convertValue(config, FeesConfig.class);
            case BILLING -> productConfigMapper.convertValue(config, BillingConfig.class);
            case LOAN_STRUCTURE -> productConfigMapper.convertValue(config, LoanStructureConfig.class);
        };
    }

    /**
     * Maps stored config JSON back to typed config
     *
     * @param productConfig Product config entity
     */
    private Object mapStoredConfig(final ProductConfig productConfig) {
        return switch (productConfig.getConfigType()) {
            case TENURE -> productConfigMapper.fromJson(productConfig.getConfigJson(), TenureConfig.class);
            case FEES -> productConfigMapper.fromJson(productConfig.getConfigJson(), FeesConfig.class);
            case BILLING -> productConfigMapper.fromJson(productConfig.getConfigJson(), BillingConfig.class);
            case LOAN_STRUCTURE ->
                    productConfigMapper.fromJson(productConfig.getConfigJson(), LoanStructureConfig.class);
        };
    }

    /**
     * Applies both structural (jakarta) and business validations
     *
     * @param typedConfig Config
     */
    private void validateTypedConfig(final Object typedConfig) {

        validateConfig(typedConfig);

        if (typedConfig instanceof TenureConfig tenure) {
            if (tenure.getMinimumValue() > tenure.getMaximumValue()) {
                throw new IllegalArgumentException("Minimum tenure cannot be greater than maximum tenure");
            }

            if (tenure.getDefaultValue() < tenure.getMinimumValue()
                    || tenure.getDefaultValue() > tenure.getMaximumValue()) {
                throw new IllegalArgumentException("Default tenure must be within minimum and maximum range");
            }
        }

        if (typedConfig instanceof FeesConfig fees) {
            validateFeeValueWhenEnabled(fees.getServiceFee(), "Service fee");
            validateFeeValueWhenEnabled(fees.getDailyFee(), "Daily fee");
            validateFeeValueWhenEnabled(fees.getLateFee(), "Late fee");
        }
    }

    /**
     * Ensures fee value is present when fee is enabled
     *
     * @param feeDetail Fee detail
     * @param feeName   Fee name
     */
    private void validateFeeValueWhenEnabled(final FeeDetailConfig feeDetail, final String feeName) {
        if (feeDetail != null && Boolean.TRUE.equals(feeDetail.getEnabled()) && feeDetail.getValue() == null) {
            throw new IllegalArgumentException(feeName + " value is required when enabled");
        }
    }

    /**
     * Bean validation for config payload
     *
     * @param config Config to validate
     */
    private <T> void validateConfig(final T config) {
        var violations = validator.validate(config);

        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .findFirst()
                    .orElse("Invalid config payload");

            throw new IllegalArgumentException("Invalid " + config.getClass().getSimpleName() + ": " + message);
        }
    }
}