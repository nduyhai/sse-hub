CREATE TABLE notifications
(
    id              BIGSERIAL PRIMARY KEY,
    notification_id VARCHAR(36)  NOT NULL UNIQUE,
    user_id         VARCHAR(255) NOT NULL,
    message         TEXT         NOT NULL,
    type            VARCHAR(100),
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMPTZ  NOT NULL,
    delivered_at    TIMESTAMPTZ
);

CREATE INDEX idx_notifications_user_id_status ON notifications (user_id, status);
CREATE INDEX idx_notifications_status ON notifications (status);
CREATE INDEX idx_notifications_created_at ON notifications (created_at);
