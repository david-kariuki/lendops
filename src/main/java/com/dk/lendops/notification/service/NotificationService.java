package com.dk.lendops.notification.service;

import com.dk.lendops.notification.dto.response.NotificationResponse;
import com.dk.lendops.notification.enums.NotificationType;
import com.dk.lendops.notification.service.impl.NotificationServiceImpl;

import java.util.List;


/**
 * Notification service
 *
 * @author David Kariuki
 * @see NotificationServiceImpl Service implementation
 */
public interface NotificationService {

    /**
     * Creates notification record
     *
     * @param customerRef Customer reference
     * @param loanRef     Loan reference
     * @param recipient   Recipient
     * @param message     Message
     * @param type        Notification type
     */
    void createNotification(
            String customerRef, String loanRef, String recipient, String message, NotificationType type);

    /**
     * Gets notifications by customer reference
     *
     * @param customerRef Customer reference
     * @return List of notifications
     */
    List<NotificationResponse> getNotificationsByCustomerRef(String customerRef);

    /**
     * Gets notifications by loan reference
     *
     * @param loanRef Loan reference
     * @return List of notifications
     */
    List<NotificationResponse> getNotificationsByLoanRef(String loanRef);
}
