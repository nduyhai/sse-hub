package com.nduyhai.ssehub.shared.event;

import java.time.Instant;

public record NotificationDeliveredEvent(
        String notificationId,
        String userId,
        Instant deliveredAt
) {}
