package com.dk.lendops.notification.service.impl;

import com.dk.lendops.notification.dto.response.NotificationResponse;
import com.dk.lendops.notification.entity.Notification;
import com.dk.lendops.notification.enums.NotificationChannel;
import com.dk.lendops.notification.enums.NotificationStatus;
import com.dk.lendops.notification.enums.NotificationType;
import com.dk.lendops.notification.repository.NotificationRepository;
import com.dk.lendops.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Notification service
 *
 * @author David Kariuki
 * @see NotificationService Service interface
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Creates notification record
     *
     * @param customerRef Customer reference
     * @param loanRef     Loan reference
     * @param recipient   Recipient
     * @param message     Message
     * @param type        Notification type
     */
    @Override
    public void createNotification(String customerRef, String loanRef, String recipient, String message, NotificationType type) {

        Notification notification = Notification.builder()
                .notificationRef(generateNotificationRef())
                .customerRef(customerRef)
                .loanRef(loanRef)
                .recipient(recipient)
                .message(message)
                .type(type)
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .build();

        notificationRepository.save(notification);

        log.debug("Created notification {} for customer {}", notification.getNotificationRef(), customerRef);

    }

    /**
     * Gets notifications by customer reference
     *
     * @param customerRef Customer reference
     * @return List of notifications
     */
    @Override
    public List<NotificationResponse> getNotificationsByCustomerRef(String customerRef) {
        return notificationRepository.findByCustomerRef(customerRef)
                .stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(this::mapResponse)
                .toList();
    }

    /**
     * Gets notifications by loan reference
     *
     * @param loanRef Loan reference
     * @return List of notifications
     */
    @Override
    public List<NotificationResponse> getNotificationsByLoanRef(String loanRef) {
        return notificationRepository.findByLoanRef(loanRef)
                .stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(this::mapResponse)
                .toList();
    }

    /**
     * Generates notification reference
     *
     * @return Notification reference
     */
    private String generateNotificationRef() {
        return "NT-".concat(UUID.randomUUID().toString().substring(0, 8));
    }

    /**
     * Maps notification entity to response
     *
     * @param notification Notification
     * @return Notification response
     */
    private NotificationResponse mapResponse(final Notification notification) {
        return NotificationResponse.builder()
                .notificationRef(notification.getNotificationRef())
                .customerRef(notification.getCustomerRef())
                .loanRef(notification.getLoanRef())
                .type(notification.getType())
                .channel(notification.getChannel())
                .recipient(notification.getRecipient())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
