package com.nduyhai.ssehub.sse.application;

import com.nduyhai.ssehub.shared.event.NotificationCreatedEvent;
import com.nduyhai.ssehub.shared.event.NotificationDeliveredEvent;
import com.nduyhai.ssehub.sse.domain.SseSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseDispatcher {

    private final SseSessionRegistry registry;
    private final ApplicationEventPublisher eventPublisher;

    public void dispatch(NotificationCreatedEvent event) {
        ServerSentEvent<NotificationCreatedEvent> sse = ServerSentEvent
                .<NotificationCreatedEvent>builder()
                .id(event.notificationId())
                .event("notification")
                .data(event)
                .build();

        boolean delivered = registry.dispatch(event.userId(), sse);
        if (delivered) {
            eventPublisher.publishEvent(
                    new NotificationDeliveredEvent(event.notificationId(), event.userId(), Instant.now()));
        } else {
            log.debug("No active SSE session for userId={}, notification {} not delivered",
                    event.userId(), event.notificationId());
        }
    }
}
