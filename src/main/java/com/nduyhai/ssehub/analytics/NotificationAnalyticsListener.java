package com.nduyhai.ssehub.analytics;

import com.nduyhai.ssehub.shared.event.NotificationCreatedEvent;
import com.nduyhai.ssehub.shared.event.NotificationDeliveredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class NotificationAnalyticsListener {

  @ApplicationModuleListener
  void onNotificationCreated(NotificationCreatedEvent event) {
    log.info(
        "analytics event=notification_created notificationId={} userId={} type={}",
        event.notificationId(),
        event.userId(),
        event.type());
  }

  @ApplicationModuleListener
  void onNotificationDelivered(NotificationDeliveredEvent event) {
    log.info(
        "analytics event=notification_delivered notificationId={} userId={} deliveredAt={}",
        event.notificationId(),
        event.userId(),
        event.deliveredAt());
  }
}
