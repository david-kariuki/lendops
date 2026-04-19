package com.dk.lendops.loan.repository;

import com.dk.lendops.loan.entity.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repayment repository
 *
 * @author David Kariuki
 */
public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

    /**
     * Finds repayment by repayment reference
     *
     * @param repaymentRef Repayment reference
     * @return Repayment
     */
    Optional<Repayment> findByRepaymentRef(String repaymentRef);

    /**
     * Checks if repayment exists by payment reference
     *
     * @param paymentReference Payment reference
     * @return True if exists
     */
    boolean existsByPaymentReference(String paymentReference);

    /**
     * Finds repayments for a loan
     *
     * @param loanId Loan ID
     * @return List of repayments
     */
    List<Repayment> findByLoanId(Long loanId);
}
