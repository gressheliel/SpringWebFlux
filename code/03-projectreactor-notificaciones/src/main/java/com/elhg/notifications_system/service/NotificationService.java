package com.elhg.notifications_system.service;

import com.elhg.notifications_system.models.NotificationEvent;
import reactor.core.publisher.Mono;

public interface NotificationService {
    public Mono<Boolean> sendNotification(NotificationEvent notificationEvent);
}
