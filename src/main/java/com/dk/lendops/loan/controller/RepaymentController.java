package com.dk.lendops.loan.controller;

import com.dk.lendops.common.response.ApiResponse;
import com.dk.lendops.common.response.ApiResponseBuilder;
import com.dk.lendops.loan.dto.request.repayment.CreateRepaymentRequest;
import com.dk.lendops.loan.dto.response.repayment.RepaymentResponse;
import com.dk.lendops.loan.service.RepaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Repayment controller
 *
 * @author David Kariuki
 * @see RepaymentService Repayment service
 */
@Tag(name = "Repayment APIs")
@RestController
@RequestMapping(
        value = RepaymentController.BASE_PATH,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RepaymentController {

    public static final String BASE_PATH = "/api/v1/repayments";

    private final RepaymentService repaymentService;
    private final ApiResponseBuilder apiResponseBuilder;

    @Operation(
            summary = "Create repayment",
            description = "Creates a repayment against a loan")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<RepaymentResponse> createRepayment(
            @RequestHeader Map<String, String> headers,
            @Valid @RequestBody CreateRepaymentRequest request) {

        RepaymentResponse response = repaymentService.createRepayment(headers, request);
        return apiResponseBuilder.success(response);
    }
}
