package com.dk.lendops.product.repository;

import com.dk.lendops.product.entity.ProductConfig;
import com.dk.lendops.product.enums.ConfigType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * ProductConfigRepository
 *
 * @author David Kariuki
 */
public interface ProductConfigRepository extends JpaRepository<ProductConfig, Long> {

    /**
     * Find all configs for a product
     *
     * @param productId Product ID
     * @return List of ProductConfig
     */
    List<ProductConfig> findByProductId(Long productId);

    /**
     * Find all active configs for a product
     *
     * @param productId Product ID
     * @return List of active ProductConfig
     */
    List<ProductConfig> findByProductIdAndActiveTrue(Long productId);

    /**
     * Find active config for a product by config type
     *
     * @param productId  Product ID
     * @param configType Config Type
     * @return Optional of ProductConfig
     */
    Optional<ProductConfig> findByProductIdAndConfigTypeAndActiveTrue(Long productId, ConfigType configType);

    /**
     * Finds latest config version for a product and config type
     *
     * @param productId  Product ID
     * @param configType Config type
     * @return Latest product config
     */
    Optional<ProductConfig> findTopByProductIdAndConfigTypeOrderByConfigVersionDesc(Long productId, ConfigType configType);
}
