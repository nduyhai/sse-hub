package com.nduyhai.ssehub.sse;

import org.springframework.context.annotation.Configuration;

/**
 * SSE module — maintains a per-user {@code SseEmitter} registry and delivers
 * notification events to connected browser clients over Server-Sent Events.
 * The {@code application} sub-package is a named interface, allowing the
 * {@code redis} module to call {@code SseDispatcher} directly.
 */
@Configuration
public class SseModule {
}
