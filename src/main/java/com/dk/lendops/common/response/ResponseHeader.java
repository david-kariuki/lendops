package com.dk.lendops.common.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * API response header
 *
 * @author David Kariuki
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseHeader {

    private String requestRefId;
    private int responseCode;
    private String message;
    private String detailedMessage;
    private LocalDateTime timestamp;
}
