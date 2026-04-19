package com.dk.lendops.loan.controller;

import com.dk.lendops.common.response.ApiResponse;
import com.dk.lendops.common.response.ApiResponseBuilder;
import com.dk.lendops.loan.dto.request.CreateLoanRequest;
import com.dk.lendops.loan.dto.response.LoanResponse;
import com.dk.lendops.loan.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Loan controller
 *
 * @author David Kariuki
 * @see LoanService Loan service
 */
@Tag(name = "Loan APIs")
@RestController
@RequestMapping(
        value = LoanController.BASE_PATH,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LoanController {

    public static final String BASE_PATH = "/api/v1/loans";

    private final LoanService loanService;
    private final ApiResponseBuilder apiResponseBuilder;

    @Operation(
            summary = "Create loan",
            description = "Creates a new loan")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<LoanResponse> createLoan(
            @RequestHeader Map<String, String> headers,
            @Valid @RequestBody CreateLoanRequest request) {

        LoanResponse response = loanService.createLoan(headers, request);
        return apiResponseBuilder.success(response);
    }

    @Operation(
            summary = "Get loan",
            description = "Fetches loan by reference")
    @GetMapping("/{loanRef}")
    public ApiResponse<LoanResponse> getLoan(
            @RequestHeader Map<String, String> headers,
            @PathVariable String loanRef) {

        LoanResponse response = loanService.getLoanByRef(headers, loanRef);
        return apiResponseBuilder.success(response);
    }
}
