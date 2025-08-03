package com.elhg;

import com.elhg.notifications_system.NotificationSystem;
import com.elhg.notifications_system.models.NotificationEvent;
import com.elhg.notifications_system.models.NotificationStatus;
import com.elhg.notifications_system.models.Priority;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
public class MainNotifications {
    public static void main(String[] args) {
        String id = UUID.randomUUID().toString();
        NotificationEvent eventHigh = NotificationEvent.builder()
                .id(id)
                .source("TEST")
                .message("Test msg with priority: " + Priority.HIGH.toString())
                .priority(Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .notificationStatus(NotificationStatus.PENDING)
                .build();

        NotificationEvent eventMedium = NotificationEvent.builder()
                .id(UUID.randomUUID().toString())
                .source("TEST")
                .message("Test msg with priority: " + Priority.MEDIUM.toString())
                .priority(Priority.MEDIUM)
                .timestamp(LocalDateTime.now())
                .notificationStatus(NotificationStatus.PENDING)
                .build();

        NotificationEvent eventLow = NotificationEvent.builder()
                .id(UUID.randomUUID().toString())
                .source("TEST")
                .message("Test msg with priority: " + Priority.LOW.toString())
                .priority(Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .notificationStatus(NotificationStatus.PENDING)
                .build();
        log.info("Created notifications with different priorities: High, Medium, Low");

        NotificationSystem notificationSystem = new NotificationSystem();
        notificationSystem.publishEvent(eventHigh);
        notificationSystem.publishEvent(eventMedium);
        notificationSystem.publishEvent(eventLow);

        log.info("Published notifications to the system.");

        log.info("Retrieving notification by ID: {}" , id);
        notificationSystem.getNotificationById(id)
                .subscribe(
                        notification -> log.info("Notification found: {}" , notification),
                        error -> log.error("Error retrieving notification: {}" , error.getMessage()),
                        () -> log.info("Notification retrieval completed successfully!")
                );


        notificationSystem.getNotificationHistory()
                .subscribe(
                        notification -> log.info("Notification in history: {}" , notification),
                        error -> log.error("Error retrieving notification history: {}" , error.getMessage()),
                        () -> log.info("Notification history retrieval completed successfully!")
                );
        log.info("Retrieving notification history...");


        notificationSystem.retryFailedNotifications()
                .subscribe(notificationEvent -> log.info("Retried failed notification: {}" , notificationEvent),
                        error -> log.error("Error retrying failed notifications: {}" , error.getMessage()),
                        () -> log.info("Retry of failed notifications completed successfully!"));
    }
}
