package com.nduyhai.ssehub.sse.api;

import com.nduyhai.ssehub.sse.application.SseConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/sse")
@RequiredArgsConstructor
class SseController {

    private final SseConnectionService connectionService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<?>> subscribe(
        @RequestParam String userId,
        @RequestParam(required = false) String deviceId) {
        return connectionService.connect(userId, deviceId);
    }
}
