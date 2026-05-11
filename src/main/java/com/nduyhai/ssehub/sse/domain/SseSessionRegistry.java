package com.nduyhai.ssehub.sse.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SseSessionRegistry {

  // One multicast sink per userId; autoCancel=false keeps the sink alive when all
  // subscribers temporarily disconnect (e.g. browser tab closed then reopened).
  private final ConcurrentHashMap<String, Sinks.Many<ServerSentEvent<?>>> sinks =
      new ConcurrentHashMap<>();

  public Flux<ServerSentEvent<?>> subscribe(String userId) {
    Sinks.Many<ServerSentEvent<?>> sink =
        sinks.computeIfAbsent(
            userId, id -> Sinks.many().multicast().onBackpressureBuffer(256, false));
    log.debug("SSE subscriber added for userId={}", userId);
    return sink.asFlux();
  }

  public boolean dispatch(String userId, ServerSentEvent<?> event) {
    Sinks.Many<ServerSentEvent<?>> sink = sinks.get(userId);
    if (sink == null) {
      log.debug("No SSE sink for userId={}", userId);
      return false;
    }
    Sinks.EmitResult result = sink.tryEmitNext(event);
    if (result.isFailure()) {
      log.warn("SSE emit failed for userId={}: {}", userId, result);
    }
    return result == Sinks.EmitResult.OK;
  }
}
