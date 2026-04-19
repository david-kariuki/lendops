package com.dk.lendops.common.controller;

import com.dk.lendops.common.response.ApiResponse;
import com.dk.lendops.common.response.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check controller
 *
 * @author David Kariuki
 */
@Tag(name = "Health Check APIs")
@RestController
@RequestMapping(
        value = HealthCheckController.BASE_PATH,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class HealthCheckController {

    public static final String BASE_PATH = "/api/v1/health";

    private final ApiResponseBuilder apiResponseBuilder;

    /**
     * Checks service health
     *
     * @return Health response
     */
    @Operation(
            summary = "Health check",
            description = "Checks if the service is up")
    @GetMapping
    public ApiResponse<Map<String, String>> healthCheck() {
        return apiResponseBuilder.success(Map.of("status", "UP"));
    }
}
