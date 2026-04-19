package com.dk.lendops.loan.repository;

import com.dk.lendops.loan.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Loan installment repository
 *
 * @author David Kariuki
 */
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    /**
     * Finds installments for a loan
     *
     * @param loanId Loan ID
     * @return List of installments
     */
    List<LoanInstallment> findByLoanId(Long loanId);
}
