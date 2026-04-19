package com.dk.lendops.customer.controller;

import com.dk.lendops.common.response.ApiResponse;
import com.dk.lendops.common.response.ApiResponseBuilder;
import com.dk.lendops.customer.dto.request.CreateCustomerRequest;
import com.dk.lendops.customer.dto.request.UpdateCustomerLimitRequest;
import com.dk.lendops.customer.dto.request.UpdateCustomerRequest;
import com.dk.lendops.customer.dto.response.CustomerResponse;
import com.dk.lendops.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Customer controller
 *
 * @author David Kariuki
 * @see CustomerService Customer service
 */
@Tag(name = "Customer APIs")
@RestController
@RequestMapping(
        value = CustomerController.BASE_PATH,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CustomerController {

    public static final String BASE_PATH = "/api/v1/customers";

    private final CustomerService customerService;
    private final ApiResponseBuilder apiResponseBuilder;

    @Operation(
            summary = "Create customer",
            description = "Creates a new customer with an initial loan limit")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CustomerResponse> createCustomer(
            @RequestHeader Map<String, String> headers, @Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(headers, request);
        return apiResponseBuilder.success(response);
    }

    @Operation(
            summary = "Get customer",
            description = "Fetches customer by reference")
    @GetMapping("/{customerRef}")
    public ApiResponse<CustomerResponse> getCustomer(
            @RequestHeader Map<String, String> headers, @PathVariable String customerRef) {
        CustomerResponse response = customerService.getCustomerByRef(headers, customerRef);
        return apiResponseBuilder.success(response);
    }

    @Operation(
            summary = "Update customer",
            description = "Updates customer details")
    @PutMapping(value = "/{customerRef}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CustomerResponse> updateCustomer(
            @RequestHeader Map<String, String> headers,
            @PathVariable String customerRef,
            @Valid @RequestBody UpdateCustomerRequest request) {
        CustomerResponse response = customerService.updateCustomer(headers, customerRef, request);
        return apiResponseBuilder.success(response);
    }

    @Operation(
            summary = "Update customer limit",
            description = "Updates customer loan limit by creating a new active limit")
    @PutMapping(value = "/{customerRef}/limit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CustomerResponse> updateCustomerLimit(
            @RequestHeader Map<String, String> headers,
            @PathVariable String customerRef,
            @Valid @RequestBody UpdateCustomerLimitRequest request) {
        CustomerResponse response = customerService.updateCustomerLimit(headers, customerRef, request);
        return apiResponseBuilder.success(response);
    }
}
