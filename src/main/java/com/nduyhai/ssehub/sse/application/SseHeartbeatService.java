package com.nduyhai.ssehub.sse.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class SseHeartbeatService {

    @Value("${ssehub.sse.heartbeat-interval-ms:30000}")
    private long heartbeatIntervalMs;

    public Flux<ServerSentEvent<?>> heartbeatFlux() {
        return Flux.interval(Duration.ofMillis(heartbeatIntervalMs))
                .map(tick -> ServerSentEvent.builder()
                        .event("heartbeat")
                        .data("")
                        .build());
    }
}
