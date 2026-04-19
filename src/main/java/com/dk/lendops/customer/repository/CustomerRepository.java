package com.dk.lendops.customer.repository;

import com.dk.lendops.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Customer repository
 *
 * @author David Kariuki
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds customer by customer reference
     *
     * @param customerRef Customer reference
     * @return Customer
     */
    Optional<Customer> findByCustomerRef(String customerRef);

    /**
     * Finds customer by phone number
     *
     * @param phoneNumber Phone number
     * @return Customer
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Finds customer by email address
     *
     * @param emailAddress Email address
     * @return Customer
     */
    Optional<Customer> findByEmailAddress(String emailAddress);

    /**
     * Checks whether customer exists by customer reference
     *
     * @param customerRef Customer reference
     * @return True if customer exists, false otherwise
     */
    boolean existsByCustomerRef(String customerRef);

    /**
     * Checks whether customer exists by phone number
     *
     * @param phoneNumber Phone number
     * @return True if customer exists, false otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Checks whether customer exists by email address
     *
     * @param emailAddress Email address
     * @return True if customer exists, false otherwise
     */
    boolean existsByEmailAddress(String emailAddress);
}
