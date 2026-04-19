package com.dk.lendops.common.exception;

import lombok.Getter;

/**
 * Business exception
 *
 * @author David Kariuki
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int responseCode;
    private final String message;
    private final String detailedMessage;

    /**
     * Creates a business exception using the same message for both
     * message and detailed message.
     *
     * @param responseCode Response code
     * @param message      Response message
     */
    public BusinessException(final int responseCode, final String message) {
        super(message);
        this.responseCode = responseCode;
        this.message = message;
        this.detailedMessage = message;
    }

    /**
     * Creates a business exception with separate message values.
     *
     * @param responseCode    Response code
     * @param message         User-facing message
     * @param detailedMessage Detailed internal message
     */
    public BusinessException(final int responseCode, final String message, final String detailedMessage) {
        super(detailedMessage);
        this.responseCode = responseCode;
        this.message = message;
        this.detailedMessage = detailedMessage;
    }
}
