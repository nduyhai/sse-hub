package com.nduyhai.ssehub.shared.event;

import java.time.Instant;

public record NotificationCreatedEvent(
        String notificationId,
        String userId,
        String message,
        String type,
        Instant createdAt
) {}
