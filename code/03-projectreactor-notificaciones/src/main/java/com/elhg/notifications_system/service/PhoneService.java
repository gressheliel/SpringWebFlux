package com.elhg.notifications_system.service;

import com.elhg.notifications_system.models.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class PhoneService implements NotificationService {

    @Override
    public Mono<Boolean> sendNotification(NotificationEvent notificationEvent) {
        // Simulate sending a notification to Microsoft Teams
        return Mono.fromCallable(() -> {
            //Simulate processing time
            Thread.sleep(1000);

            //Simulate error with 20% probability
            if (ThreadLocalRandom.current().nextInt(100) < 20) {
                throw new RuntimeException("Error on send notification to Phone");
            }

            // Logic to send notification to Teams
            log.info("Sending notification to Phone OK : {}", notificationEvent);
            return true; // Return true if successful
        });
    }
}
