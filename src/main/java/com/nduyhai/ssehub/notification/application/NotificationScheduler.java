package com.nduyhai.ssehub.notification.application;

import com.nduyhai.ssehub.notification.domain.Notification;
import com.nduyhai.ssehub.notification.domain.NotificationRepository;
import com.nduyhai.ssehub.shared.event.NotificationCreatedEvent;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
class NotificationScheduler {

  private final NotificationRepository repository;
  private final ApplicationEventPublisher eventPublisher;

  // Retries notifications that were created but not delivered within 5 minutes.
  // Covers cases where the SSE session connects after the initial publication.
  @Scheduled(
      initialDelayString = "${ssehub.scheduler.retry-delay-ms:60000}",
      fixedDelayString = "${ssehub.scheduler.retry-delay-ms:60000}")
  @SchedulerLock(
      name = "retryPendingNotifications",
      lockAtMostFor = "PT2M",
      lockAtLeastFor = "PT30S")
  @Transactional
  void retryPendingNotifications() {
    Instant threshold = Instant.now().minus(5, ChronoUnit.MINUTES);
    List<Notification> pending = repository.findPendingOlderThan(threshold);
    if (pending.isEmpty()) return;

    log.info("Retrying {} pending notification(s)", pending.size());
    pending.forEach(
        n ->
            eventPublisher.publishEvent(
                new NotificationCreatedEvent(
                    n.getNotificationId(),
                    n.getUserId(),
                    n.getMessage(),
                    n.getType(),
                    n.getCreatedAt())));
  }
}
