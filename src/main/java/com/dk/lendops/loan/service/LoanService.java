package com.dk.lendops.loan.service;

import com.dk.lendops.loan.dto.request.CreateLoanRequest;
import com.dk.lendops.loan.dto.response.LoanResponse;
import com.dk.lendops.loan.service.impl.LoanServiceImpl;

import java.util.Map;

/**
 * Loan service
 *
 * @author David Kariuki
 * @see LoanServiceImpl Service Implementation
 */
public interface LoanService {

    /**
     * Creates loan
     *
     * @param headers Headers
     * @param request Loan request
     * @return Loan response
     */
    LoanResponse createLoan(Map<String, String> headers, CreateLoanRequest request);

    /**
     * Gets loan by reference
     *
     * @param headers Headers
     * @param loanRef Loan reference
     * @return Loan response
     */
    LoanResponse getLoanByRef(Map<String, String> headers, String loanRef);

    /**
     * Processes overdue loans and installments
     */
    void processOverdueLoans();
}
