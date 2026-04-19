package com.dk.lendops.common.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Builds standard API responses
 *
 * @author David Kariuki
 */
@Slf4j
@Component
public class ApiResponseBuilder {

    /**
     * Builds a successful response
     *
     * @param body Response body
     * @param <T>  Type
     * @return ApiResponse
     */
    public <T> ApiResponse<T> success(T body) {

        String requestRefId = generateRefId();
        log.debug("Success response, requestRefId={}", requestRefId);

        return ApiResponse.<T>builder()
                .header(ResponseHeader.builder()
                        .requestRefId(requestRefId)
                        .responseCode(200)
                        .technicalMessage("Success")
                        .customerMessage("Request executed successfully")
                        .timestamp(LocalDateTime.now())
                        .build())
                .body(body)
                .build();
    }

    /**
     * Builds a failure response
     *
     * @param code             Response code
     * @param technicalMessage Technical message
     * @param customerMessage  Customer message
     * @param <T>              Type
     * @return ApiResponse
     */
    public <T> ApiResponse<T> failure(int code, String technicalMessage, String customerMessage) {

        String requestRefId = generateRefId();
        log.debug("Failure response, requestRefId={}, code={}", requestRefId, code);


        return ApiResponse.<T>builder()
                .header(ResponseHeader.builder()
                        .requestRefId(requestRefId)
                        .responseCode(code)
                        .technicalMessage(technicalMessage)
                        .customerMessage(customerMessage)
                        .timestamp(LocalDateTime.now())
                        .build())
                .body(null)
                .build();
    }

    /**
     * Generates a unique reference ID for the request
     *
     * @return Unique reference ID
     */
    private String generateRefId() {
        return UUID.randomUUID().toString();
    }
}
