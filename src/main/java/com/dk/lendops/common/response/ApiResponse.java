package com.dk.lendops.common.response;

import lombok.*;

/**
 * Standard API response wrapper
 *
 * @author David Kariuki
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private ResponseHeader header;
    private T body;
}
