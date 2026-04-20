package com.dk.lendops.notification.repository;

import com.dk.lendops.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Notification repository
 *
 * @author David Kariuki
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Finds notifications by customer reference
     *
     * @param customerRef Customer reference
     * @return List of notifications
     */
    List<Notification> findByCustomerRef(String customerRef);

    /**
     * Finds notifications by loan reference
     *
     * @param loanRef Loan reference
     * @return List of notifications
     */
    List<Notification> findByLoanRef(String loanRef);
}
