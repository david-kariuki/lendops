package com.dk.lendops.notification.controller;

import com.dk.lendops.common.response.ApiResponse;
import com.dk.lendops.common.response.ApiResponseBuilder;
import com.dk.lendops.notification.dto.response.NotificationResponse;
import com.dk.lendops.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Notification controller
 *
 * @author David Kariuki
 * @see NotificationService Notification service
 */
@Tag(name = "Notification APIs")
@RestController
@RequestMapping(
        value = NotificationController.BASE_PATH,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NotificationController {

    public static final String BASE_PATH = "/api/v1/notifications";

    private final NotificationService notificationService;
    private final ApiResponseBuilder apiResponseBuilder;

    @Operation(
            summary = "Get notifications by customer reference",
            description = "Fetches notifications for a given customer reference")
    @GetMapping("/customers/{customerRef}")
    public ApiResponse<List<NotificationResponse>> getNotificationsByCustomerRef(
            @RequestHeader Map<String, String> headers,
            @PathVariable String customerRef) {
        List<NotificationResponse> response = notificationService.getNotificationsByCustomerRef(customerRef);
        return apiResponseBuilder.success(response);
    }

    @Operation(
            summary = "Get notifications by loan reference",
            description = "Fetches notifications for a given loan reference")
    @GetMapping("/loans/{loanRef}")
    public ApiResponse<List<NotificationResponse>> getNotificationsByLoanRef(
            @RequestHeader Map<String, String> headers,
            @PathVariable String loanRef) {

        List<NotificationResponse> response = notificationService.getNotificationsByLoanRef(loanRef);
        return apiResponseBuilder.success(response);
    }
}
