package com.dk.lendops.common.exception;

import com.dk.lendops.common.response.ApiResponse;
import com.dk.lendops.common.response.ApiResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler
 *
 * @author David Kariuki
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiResponseBuilder apiResponseBuilder;

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error(exception.getMessage(), exception);
        return apiResponseBuilder.failure(
                409,
                exception.getMessage(),
                "Sorry, request could not be processed!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException exception) {
        log.error("Validation failed", exception);

        // Get exact technical message for cases where I am using annotated validations, e.g., Jakarta
        String technicalMessage = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed!");

        return apiResponseBuilder.failure(
                400,
                technicalMessage,
                "Sorry, request could not be processed!");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGenericException(Exception exception) {
        log.error("Unhandled exception", exception);
        return apiResponseBuilder.failure(
                500,
                exception.getMessage(),
                "Sorry, something went terribly wrong!");
    }
}