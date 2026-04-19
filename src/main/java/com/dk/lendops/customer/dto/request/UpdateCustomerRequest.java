package com.dk.lendops.customer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Request to update customer details
 *
 * @author David Kariuki
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@AllArgsConstructor
public class UpdateCustomerRequest extends BaseCustomerRequest {
}