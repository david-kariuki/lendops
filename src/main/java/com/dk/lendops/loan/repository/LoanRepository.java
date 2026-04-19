package com.dk.lendops.loan.repository;

import com.dk.lendops.loan.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Loan repository
 *
 * @author David Kariuki
 */
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /**
     * Finds loan by reference
     *
     * @param loanRef Loan reference
     * @return Loan
     */
    Optional<Loan> findByLoanRef(String loanRef);

    /**
     * Checks if loan exists by reference
     *
     * @param loanRef Loan reference
     * @return True if exists
     */
    boolean existsByLoanRef(String loanRef);
}
