package com.dk.lendops.notification.dto.response;

import com.dk.lendops.notification.enums.NotificationChannel;
import com.dk.lendops.notification.enums.NotificationStatus;
import com.dk.lendops.notification.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Notification response
 *
 * @author David Kariuki
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationResponse {

    private String notificationRef;
    private String customerRef;
    private String loanRef;
    private NotificationType type;
    private NotificationChannel channel;
    private String recipient;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
