package com.nduyhai.ssehub.redis;

import org.springframework.context.annotation.Configuration;

/**
 * Redis module — bridges the Spring Modulith event bus to Redis Pub/Sub.
 * Publishes {@code NotificationCreatedEvent} to per-user channels so that
 * any sse-hub instance (including others in the cluster) can deliver the
 * notification to the browser holding the SSE connection.
 */
@Configuration
public class RedisModule {
}
