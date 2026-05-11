# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw package -DskipTests

# Run (starts PostgreSQL + Redis via Docker Compose automatically)
./mvnw spring-boot:run

# Run all tests (Testcontainers spins up PostgreSQL + Redis automatically)
./mvnw test

# Run a single test class
./mvnw test -Dtest=SseHubApplicationTests

# Run a single test method
./mvnw test -Dtest=SseHubApplicationTests#contextLoads
```

## Architecture

This is a **Spring Modulith** application (Spring Boot 4, Java 25) acting as a Server-Sent Events hub: it bridges internal microservices to browser clients via Redis Pub/Sub, enabling horizontal scaling.

### Module map (`com.tripzy.ssehub.*`)

| Module | Key classes | Responsibility |
|---|---|---|
| `shared` | `NotificationCreatedEvent`, `NotificationDeliveredEvent` | Open module — shared events used across all modules |
| `notification` | `NotificationController`, `NotificationService`, `NotificationScheduler` | Accepts `POST /api/v1/notifications`, persists, publishes `NotificationCreatedEvent`; retries PENDING notifications on a schedule |
| `sse` | `SseController`, `SseConnectionService`, `SseDispatcher`, `SseSessionRegistry` | Manages `SseEmitter` registry per `userId`; serves `GET /api/v1/sse/subscribe?userId=`; sends heartbeats every 30 s |
| `redis` | `RedisNotificationPublisher`, `RedisNotificationSubscriber` | Receives `NotificationCreatedEvent` via Modulith event bus → publishes to Redis channel `notification:user:{userId}`; subscribes to pattern `notification:user:*` → calls `SseDispatcher` |
| `analytics` | `NotificationAnalyticsListener` | Listens to `NotificationCreatedEvent` and `NotificationDeliveredEvent` via Modulith event bus; emits structured log entries |

### End-to-end data flow

1. Browser connects: `GET /api/v1/sse/subscribe?userId=U1` → `SseSessionRegistry`, receives `event: connected`
2. Internal service fires: `POST /api/v1/notifications` → `NotificationService` saves to DB, publishes `NotificationCreatedEvent`
3. After transaction commit, Spring Modulith delivers the event asynchronously to:
   - `RedisNotificationPublisher` → `PUBLISH notification:user:U1` on Redis
   - `NotificationAnalyticsListener` → structured log
4. Every `sse-hub` instance subscribes to `notification:user:*` — the instance holding U1's SSE connection receives the message via `RedisNotificationSubscriber` → `SseDispatcher.dispatch()` → `event: notification` to browser
5. On delivery, `SseDispatcher` publishes `NotificationDeliveredEvent` → `NotificationService.onNotificationDelivered()` marks the DB record `DELIVERED`

### Key design constraints

- **Spring Modulith module boundaries are enforced.** Cross-module communication uses the event bus (`@ApplicationModuleListener`), except for the `redis → sse` path (see below). Violating boundaries is caught by `@ApplicationModuleTest`.
- **`sse.application` is a named interface** (`@NamedInterface` on the package). This lets the `redis` module call `SseDispatcher.dispatch()` directly — the only approved cross-module method call. The `redis` module declares `allowedDependencies = {"shared", "sse :: application"}`.
- **Redis Pub/Sub enables horizontal scaling.** The publisher and subscriber run in the same process; in a multi-instance deployment, any instance's publisher puts the message on Redis, and whichever instance holds the user's SSE connection delivers it.
- **`NotificationScheduler` retries PENDING notifications older than 5 minutes.** This covers cases where the user's browser connects after a notification was published.
- **`SseEmitter` is WebFlux.

### Infrastructure

- **PostgreSQL** — notifications persisted via Spring Data JPA + Flyway migrations (`src/main/resources/db/migration/`)
- **Redis** — Pub/Sub only (no cache); `StringRedisTemplate` publishes JSON; `RedisMessageListenerContainer` with `PatternTopic` subscribes
- **Docker Compose** (`docker-compose.yml`) — auto-started by Spring Boot in dev (`spring.docker.compose.lifecycle-management: start-and-stop`); Testcontainers used in tests

### Tech stack

- Java 25, Spring Boot 4.0.6, Spring Modulith 2.0.6
- Spring MVC for SSE via `SseEmitter`
- Spring Data JPA + Flyway + PostgreSQL
- Spring Data Redis for Pub/Sub
- Lombok
- Spring Actuator + Modulith Actuator (`/actuator/modulith`)
