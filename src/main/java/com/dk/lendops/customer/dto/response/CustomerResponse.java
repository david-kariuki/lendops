package com.dk.lendops.customer.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Customer response
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerResponse {

    private Long id;
    private String customerRef;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;
    private String status;
    private BigDecimal loanLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
