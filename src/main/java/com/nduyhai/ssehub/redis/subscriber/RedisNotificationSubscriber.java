package com.nduyhai.ssehub.redis.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nduyhai.ssehub.redis.config.RedisConfig;
import com.nduyhai.ssehub.shared.event.NotificationCreatedEvent;
import com.nduyhai.ssehub.sse.application.SseDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisNotificationSubscriber implements MessageListener {

    private final SseDispatcher sseDispatcher;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String userId = channel.replace(RedisConfig.CHANNEL_PREFIX, "");

        try {
            NotificationCreatedEvent event = objectMapper.readValue(message.getBody(), NotificationCreatedEvent.class);
            log.debug("Received Redis message for userId={}, notificationId={}", userId, event.notificationId());
            sseDispatcher.dispatch(event);
        } catch (Exception e) {
            log.error("Failed to process Redis message from channel={}", channel, e);
        }
    }
}
