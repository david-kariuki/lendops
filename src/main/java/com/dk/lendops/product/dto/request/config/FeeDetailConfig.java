package com.dk.lendops.product.dto.request.config;

import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Fee detail config
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeeDetailConfig {

    private Boolean enabled;
    private String calculationType;

    @Positive(message = "Fee value must be greater than zero")
    private Integer value;

    private String applicationTiming;
    private Integer triggerAfterDays;
}