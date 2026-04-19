package com.dk.lendops.customer.service.impl;

import com.dk.lendops.common.exception.BusinessException;
import com.dk.lendops.customer.dto.request.CreateCustomerRequest;
import com.dk.lendops.customer.dto.request.UpdateCustomerLimitRequest;
import com.dk.lendops.customer.dto.request.UpdateCustomerRequest;
import com.dk.lendops.customer.dto.response.CustomerResponse;
import com.dk.lendops.customer.entity.Customer;
import com.dk.lendops.customer.entity.CustomerLimit;
import com.dk.lendops.customer.enums.CustomerStatus;
import com.dk.lendops.customer.repository.CustomerLimitRepository;
import com.dk.lendops.customer.repository.CustomerRepository;
import com.dk.lendops.customer.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Customer service
 *
 * @author David Kariuki
 * @see CustomerService Customer service interface
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerLimitRepository customerLimitRepository;

    /**
     * Creates a customer with initial active limit
     *
     * @param headers Headers
     * @param request Customer creation request
     * @return Customer response
     */
    @Override
    @Transactional
    public CustomerResponse createCustomer(Map<String, String> headers, CreateCustomerRequest request) {

        // Prevent duplicate customer creation by reference
        if (customerRepository.existsByCustomerRef(request.getCustomerRef())) {
            throw new BusinessException(
                    409,
                    "Customer already exists!",
                    "Customer with reference " + request.getCustomerRef() + " already exists!");
        }

        // Prevent duplicate customer creation by phone number
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException(
                    409,
                    "Customer with that phone number already exists!",
                    "Customer with phone number " + request.getPhoneNumber() + " already exists!");
        }

        // Prevent duplicate customer creation by email address
        if (customerRepository.existsByEmailAddress(request.getEmailAddress())) {
            throw new BusinessException(
                    409,
                    "Customer with that email address already exists!",
                    "Customer with email address " + request.getEmailAddress() + " already exists!");
        }

        Customer customer = Customer.builder()
                .customerRef(request.getCustomerRef())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .emailAddress(request.getEmailAddress())
                .status(CustomerStatus.valueOf(request.getStatus()))
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        CustomerLimit customerLimit = CustomerLimit.builder()
                .customer(savedCustomer)
                .limitAmount(request.getLoanLimit())
                .active(true)
                .build();

        customerLimitRepository.save(customerLimit);

        log.debug("Saved customer {}", savedCustomer.getCustomerRef());

        return getCustomerByRef(headers, savedCustomer.getCustomerRef());
    }

    /**
     * Gets customer by reference
     *
     * @param headers     Headers
     * @param customerRef Customer reference
     * @return Customer response
     */
    @Override
    public CustomerResponse getCustomerByRef(Map<String, String> headers, String customerRef) {

        Customer customer = customerRepository.findByCustomerRef(customerRef)
                .orElseThrow(() -> new BusinessException(
                        404,
                        "Customer not found!",
                        "Customer not found with ref : " + customerRef));

        // Fetch active limit only
        CustomerLimit activeLimit = customerLimitRepository.findByCustomerIdAndActiveTrue(customer.getId())
                .orElseThrow(() -> new BusinessException(
                        404,
                        "Customer limit not found!",
                        "No active limit found for customer with ref : " + customerRef));

        return CustomerResponse.builder()
                .id(customer.getId())
                .customerRef(customer.getCustomerRef())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .emailAddress(customer.getEmailAddress())
                .status(customer.getStatus().name())
                .loanLimit(activeLimit.getLimitAmount())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    /**
     * Updates customer details
     *
     * @param headers     Headers
     * @param customerRef Customer reference
     * @param request     Customer update request
     * @return Customer response
     */
    @Override
    public CustomerResponse updateCustomer(Map<String, String> headers, String customerRef, UpdateCustomerRequest request) {

        Customer customer = customerRepository.findByCustomerRef(customerRef)
                .orElseThrow(() -> new BusinessException(
                        404,
                        "Customer not found!",
                        "Customer not found with ref : " + customerRef));

        // Allow same phone number for same customer, and block others
        customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .filter(existingCustomer -> !existingCustomer.getId().equals(customer.getId()))
                .ifPresent(existingCustomer -> {
                    throw new BusinessException(
                            409,
                            "Customer already exists!",
                            "Customer with phone number " + request.getPhoneNumber() + " already exists");
                });

        // Allow same email address for same customer, and block others
        customerRepository.findByEmailAddress(request.getEmailAddress())
                .filter(existingCustomer -> !existingCustomer.getId().equals(customer.getId()))
                .ifPresent(existingCustomer -> {
                    throw new BusinessException(
                            409,
                            "Customer with that email address already exists!",
                            "Customer with email address " + request.getEmailAddress() + " already exists");
                });

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmailAddress(request.getEmailAddress());
        customer.setStatus(CustomerStatus.valueOf(request.getStatus()));

        customerRepository.save(customer);

        log.debug("Updated customer {}", customer.getCustomerRef());

        return getCustomerByRef(headers, customer.getCustomerRef());
    }

    /**
     * Updates customer loan limit by creating a new active limit
     *
     * @param headers     Headers
     * @param customerRef Customer reference
     * @param request     Customer limit update request
     * @return Customer response
     */
    @Override
    public CustomerResponse updateCustomerLimit(
            Map<String, String> headers, String customerRef, UpdateCustomerLimitRequest request) {

        Customer customer = customerRepository.findByCustomerRef(customerRef)
                .orElseThrow(() -> new BusinessException(
                        404,
                        "Customer not found!",
                        "Customer not found with ref : " + customerRef));

        // Deactivate current active limit if present
        customerLimitRepository.findByCustomerIdAndActiveTrue(customer.getId())
                .ifPresent(activeLimit -> {
                    activeLimit.setActive(false);
                    customerLimitRepository.save(activeLimit);
                    log.debug("Deactivated active limit {} for customer {}", activeLimit.getId(), customerRef);
                });

        // Persist new active limit
        CustomerLimit newLimit = CustomerLimit.builder()
                .customer(customer)
                .limitAmount(request.getLoanLimit())
                .active(true)
                .build();

        customerLimitRepository.save(newLimit);

        log.debug("Saved new active limit {} for customer {}", request.getLoanLimit(), customerRef);

        return getCustomerByRef(headers, customerRef);
    }
}
