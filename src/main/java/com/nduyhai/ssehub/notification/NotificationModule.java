package com.nduyhai.ssehub.notification;

import org.springframework.context.annotation.Configuration;

/**
 * Notification module — accepts inbound notification requests, persists them, and publishes {@code
 * NotificationCreatedEvent} via the Spring Modulith event bus. Delivery status is updated when
 * {@code NotificationDeliveredEvent} is received.
 */
@Configuration
public class NotificationModule {}
