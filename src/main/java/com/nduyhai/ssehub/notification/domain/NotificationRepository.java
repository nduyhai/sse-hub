package com.nduyhai.ssehub.notification.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

  Notification save(Notification notification);

  Optional<Notification> findByNotificationId(String notificationId);

  List<Notification> findPendingOlderThan(Instant threshold);
}
