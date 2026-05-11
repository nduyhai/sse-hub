package com.nduyhai.ssehub.notification.application;

import com.nduyhai.ssehub.notification.api.request.SendNotificationRequest;
import com.nduyhai.ssehub.notification.api.response.NotificationResponse;
import com.nduyhai.ssehub.notification.domain.Notification;
import com.nduyhai.ssehub.notification.domain.NotificationRepository;
import com.nduyhai.ssehub.notification.domain.NotificationStatus;
import com.nduyhai.ssehub.shared.event.NotificationCreatedEvent;
import com.nduyhai.ssehub.shared.event.NotificationDeliveredEvent;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationTemplateService templateService;

    @Transactional
    public NotificationResponse send(SendNotificationRequest request) {
        String notificationId = UUID.randomUUID().toString();
        String renderedMessage = templateService.render(request.message(), request.templateVariables());

        Notification notification = new Notification();
        notification.setNotificationId(notificationId);
        notification.setUserId(request.userId());
        notification.setMessage(renderedMessage);
        notification.setType(request.type());

        repository.save(notification);

        eventPublisher.publishEvent(new NotificationCreatedEvent(
                notificationId,
                request.userId(),
                renderedMessage,
                request.type(),
                Instant.now()
        ));

        log.debug("Notification {} created for userId={}", notificationId, request.userId());
        return new NotificationResponse(notificationId, NotificationStatus.PENDING.name());
    }

    @EventListener
    @Transactional
    public void onNotificationDelivered(NotificationDeliveredEvent event) {
        repository.findByNotificationId(event.notificationId()).ifPresent(notification -> {
            notification.setStatus(NotificationStatus.DELIVERED);
            notification.setDeliveredAt(event.deliveredAt());
            repository.save(notification);
            log.debug("Notification {} marked as DELIVERED", event.notificationId());
        });
    }
}
