package com.nduyhai.ssehub.sse.application;

import com.nduyhai.ssehub.sse.domain.SseSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseConnectionService {

    private final SseSessionRegistry registry;
    private final SseHeartbeatService heartbeatService;

    public Flux<ServerSentEvent<?>> connect(String userId) {
        log.info("SSE connection established for userId={}", userId);

        ServerSentEvent<String> connected = ServerSentEvent.<String>builder()
                .event("connected")
                .data("connected")
                .build();

        Flux<ServerSentEvent<?>> events = registry.subscribe(userId);
        Flux<ServerSentEvent<?>> heartbeat = heartbeatService.heartbeatFlux();

        return Flux.concat(
                Flux.just(connected),
                Flux.merge(events, heartbeat)
        );
    }
}
