package com.nduyhai.ssehub.sse.application;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class SseHeartbeatService {

  private final Flux<ServerSentEvent<?>> heartbeat;

  public SseHeartbeatService(@Value("${ssehub.sse.heartbeat-interval-ms:30000}") long intervalMs) {
    ServerSentEvent<String> event =
        ServerSentEvent.<String>builder().event("heartbeat").data("").build();
    // share() multicasts one upstream timer to all SSE connections instead of one timer per
    // connection
    this.heartbeat =
        Flux.interval(Duration.ofMillis(intervalMs)).<ServerSentEvent<?>>map(tick -> event).share();
  }

  public Flux<ServerSentEvent<?>> heartbeatFlux() {
    return heartbeat;
  }
}
