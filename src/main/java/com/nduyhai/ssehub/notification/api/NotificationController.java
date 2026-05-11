package com.nduyhai.ssehub.notification.api;

import com.nduyhai.ssehub.notification.api.request.SendNotificationRequest;
import com.nduyhai.ssehub.notification.api.response.NotificationResponse;
import com.nduyhai.ssehub.notification.application.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
class NotificationController {

  private final NotificationService notificationService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Mono<NotificationResponse> send(@RequestBody @Validated SendNotificationRequest request) {
    // notificationService.send() is blocking (JPA) — offload to bounded-elastic scheduler
    return Mono.fromCallable(() -> notificationService.send(request))
        .subscribeOn(Schedulers.boundedElastic());
  }
}
