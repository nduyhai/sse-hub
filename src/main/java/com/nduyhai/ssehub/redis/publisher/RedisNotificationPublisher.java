package com.nduyhai.ssehub.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nduyhai.ssehub.redis.config.RedisConfig;
import com.nduyhai.ssehub.shared.event.NotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisNotificationPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @ApplicationModuleListener
    public void onNotificationCreated(NotificationCreatedEvent event) {
        String channel = RedisConfig.CHANNEL_PREFIX + event.userId();
        try {
            String payload = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, payload);
            log.debug("Published notificationId={} to channel={}", event.notificationId(), channel);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize notification event notificationId={}", event.notificationId(), e);
        }
    }
}
