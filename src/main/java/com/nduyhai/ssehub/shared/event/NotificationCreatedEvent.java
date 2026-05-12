package com.nduyhai.ssehub.shared.event;

import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record NotificationCreatedEvent(
    String notificationId,
    String userId,
    String message,
    @Nullable String type,
    Instant createdAt) {}
