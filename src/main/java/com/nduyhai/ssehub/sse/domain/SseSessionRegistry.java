package com.nduyhai.ssehub.sse.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Slf4j
public class SseSessionRegistry {

  private static final int BUFFER_SIZE = 256;

  // userId → (connectionId → sink); connectionId is the key linking a sink to its tab/session.
  private final Map<String, Map<String, Sinks.Many<ServerSentEvent<?>>>> sinks =
      new ConcurrentHashMap<>();

  public Flux<ServerSentEvent<?>> subscribe(String userId, String deviceId) {
    Sinks.Many<ServerSentEvent<?>> sink =
        Sinks.many().multicast().onBackpressureBuffer(BUFFER_SIZE, false);
    sinks.computeIfAbsent(userId, id -> new ConcurrentHashMap<>()).put(deviceId, sink);
    log.debug("SSE connection registered userId={} deviceId={}", userId, deviceId);
    return sink.asFlux().doFinally(signal -> release(userId, deviceId, sink));
  }

  public boolean dispatch(String userId, ServerSentEvent<?> event) {
    Map<String, Sinks.Many<ServerSentEvent<?>>> userSinks = sinks.get(userId);
    if (userSinks == null || userSinks.isEmpty()) {
      log.debug("No active SSE session for userId={}", userId);
      return false;
    }
    boolean delivered = false;
    for (Map.Entry<String, Sinks.Many<ServerSentEvent<?>>> entry : userSinks.entrySet()) {
      Sinks.EmitResult result = entry.getValue().tryEmitNext(event);
      if (result == Sinks.EmitResult.OK) {
        delivered = true;
      } else {
        log.warn("SSE emit failed userId={} connectionId={}: {}", userId, entry.getKey(), result);
      }
    }
    return delivered;
  }

  private void release(String userId, String deviceId, Sinks.Many<ServerSentEvent<?>> sink) {
    sink.tryEmitComplete();
    Map<String, Sinks.Many<ServerSentEvent<?>>> userSinks = sinks.get(userId);
    if (userSinks != null) {
      userSinks.remove(deviceId);
      if (userSinks.isEmpty()) {
        sinks.remove(userId, userSinks);
      }
    }
    log.debug("SSE connection released userId={} deviceId={}", userId, deviceId);
  }
}
