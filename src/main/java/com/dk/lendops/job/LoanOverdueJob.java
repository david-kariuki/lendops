package com.dk.lendops.job;

import com.dk.lendops.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Loan overdue job
 *
 * @author David Kariuki
 * @see LoanService Loan service
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoanOverdueJob {

    private final LoanService loanService;

    /**
     * Runs overdue loan processing
     */
    // @Scheduled(cron = "0 0 0 * * *") Midninght
    @Scheduled(fixedDelay = 120000) // 120 seconds
    public void processOverdueLoans() {
        log.debug("Starting overdue loan processing job");

        loanService.processOverdueLoans();

        log.debug("Completed overdue loan processing job");
    }

}
