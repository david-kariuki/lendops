package com.dk.lendops.product.repository;

import com.dk.lendops.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ProductRepository
 *
 * @author David Kariuki
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by code
     *
     * @param code Code
     * @return Product
     */
    Optional<Product> findByCode(String code);

    /**
     * Check if product exists by code
     *
     * @param code Code
     * @return True if exists, false otherwise
     */
    boolean existsByCode(String code);
}
