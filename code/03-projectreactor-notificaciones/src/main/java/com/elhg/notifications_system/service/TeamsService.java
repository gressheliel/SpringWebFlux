package com.elhg.notifications_system.service;

import com.elhg.notifications_system.models.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class TeamsService implements NotificationService {

    @Override
    public Mono<Boolean> sendNotification(NotificationEvent notificationEvent) {
        // Simulate sending a notification to Microsoft Teams
        return Mono.fromCallable(() -> {
            //Simulate processing time
            Thread.sleep(150);

            //Simulate error with 10% probability
            if(ThreadLocalRandom.current().nextInt(100) < 10) {
                throw new RuntimeException("Error on send notification to Teams");
            }

             // Logic to send notification to Teams
            log.info("Sending notification to Teams OK : {}", notificationEvent);
            return true; // Return true if successful
        });
    }
}
