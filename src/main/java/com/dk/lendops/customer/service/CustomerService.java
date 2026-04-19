package com.dk.lendops.customer.service;

import com.dk.lendops.customer.dto.request.CreateCustomerRequest;
import com.dk.lendops.customer.dto.request.UpdateCustomerLimitRequest;
import com.dk.lendops.customer.dto.request.UpdateCustomerRequest;
import com.dk.lendops.customer.dto.response.CustomerResponse;
import com.dk.lendops.customer.service.impl.CustomerServiceImpl;

import java.util.Map;

/**
 * Customer service
 *
 * @author David Kariuki
 * @see CustomerServiceImpl Customer service implementation
 */
public interface CustomerService {

    /**
     * Creates a customer with initial active limit
     *
     * @param headers Headers
     * @param request Customer creation request
     * @return Customer response
     */
    CustomerResponse createCustomer(Map<String, String> headers, CreateCustomerRequest request);

    /**
     * Gets customer by reference
     *
     * @param headers     Headers
     * @param customerRef Customer reference
     * @return Customer response
     */
    CustomerResponse getCustomerByRef(Map<String, String> headers, String customerRef);

    /**
     * Updates customer details
     *
     * @param headers     Headers
     * @param customerRef Customer reference
     * @param request     Customer update request
     * @return Customer response
     */
    CustomerResponse updateCustomer(Map<String, String> headers, String customerRef, UpdateCustomerRequest request);

    /**
     * Updates customer loan limit by creating a new active limit
     *
     * @param headers     Headers
     * @param customerRef Customer reference
     * @param request     Customer limit update request
     * @return Customer response
     */
    CustomerResponse updateCustomerLimit(
            final Map<String, String> headers, final String customerRef, final UpdateCustomerLimitRequest request);
}
