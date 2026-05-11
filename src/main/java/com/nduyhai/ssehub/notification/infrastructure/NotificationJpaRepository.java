package com.nduyhai.ssehub.notification.infrastructure;

import com.nduyhai.ssehub.notification.domain.Notification;
import com.nduyhai.ssehub.notification.domain.NotificationRepository;
import com.nduyhai.ssehub.notification.domain.NotificationStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

interface NotificationDataRepository extends JpaRepository<Notification, Long> {
  Optional<Notification> findByNotificationId(String notificationId);

  List<Notification> findByStatusAndCreatedAtBefore(NotificationStatus status, Instant threshold);
}

@Repository
class NotificationJpaRepository implements NotificationRepository {

  private final NotificationDataRepository data;

  NotificationJpaRepository(NotificationDataRepository data) {
    this.data = data;
  }

  @Override
  public Notification save(Notification notification) {
    return data.save(notification);
  }

  @Override
  public Optional<Notification> findByNotificationId(String notificationId) {
    return data.findByNotificationId(notificationId);
  }

  @Override
  public List<Notification> findPendingOlderThan(Instant threshold) {
    return data.findByStatusAndCreatedAtBefore(NotificationStatus.PENDING, threshold);
  }
}
