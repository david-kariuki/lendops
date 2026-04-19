package com.dk.lendops.customer.repository;

import com.dk.lendops.customer.entity.CustomerLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Customer limit repository
 *
 * @author David Kariuki
 */
public interface CustomerLimitRepository extends JpaRepository<CustomerLimit, Long> {

    /**
     * Finds all limits for a customer
     *
     * @param customerId Customer ID
     * @return List of customer limits
     */
    List<CustomerLimit> findByCustomerId(Long customerId);

    /**
     * Finds active limit for a customer
     *
     * @param customerId Customer ID
     * @return Active customer limit
     */
    Optional<CustomerLimit> findByCustomerIdAndActiveTrue(Long customerId);
}
