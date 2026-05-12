package com.nduyhai.ssehub.sse.application;

import com.nduyhai.ssehub.sse.domain.SseSessionRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseConnectionService {

    private final SseSessionRegistry registry;
    private final SseHeartbeatService heartbeatService;

    public Flux<ServerSentEvent<?>> connect(String userId, @Nullable String deviceId) {
        String resolvedDeviceId = StringUtils.hasText(deviceId) ? deviceId : UUID.randomUUID().toString();
        log.info("SSE connection established userId={} deviceId={}", userId, resolvedDeviceId);

        ServerSentEvent<String> connected = ServerSentEvent.<String>builder()
                .event("connected")
                .data("connected")
                .build();

        Flux<ServerSentEvent<?>> events = registry.subscribe(userId, resolvedDeviceId);
        Flux<ServerSentEvent<?>> heartbeat = heartbeatService.heartbeatFlux();

        return Flux.concat(
                Flux.just(connected),
                Flux.merge(events, heartbeat)
        );
    }
}
