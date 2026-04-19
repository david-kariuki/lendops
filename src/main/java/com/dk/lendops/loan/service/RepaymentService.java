package com.dk.lendops.loan.service;

import com.dk.lendops.loan.dto.request.repayment.CreateRepaymentRequest;
import com.dk.lendops.loan.dto.response.repayment.RepaymentResponse;
import com.dk.lendops.loan.service.impl.RepaymentServiceImpl;

import java.util.Map;

/**
 * Repayment service
 *
 * @author David Kariuki
 * @see RepaymentServiceImpl Service implementation
 */
public interface RepaymentService {

    /**
     * Creates repayment
     *
     * @param headers Headers
     * @param request Repayment request
     * @return Repayment response
     */
    RepaymentResponse createRepayment(Map<String, String> headers, CreateRepaymentRequest request);
}
