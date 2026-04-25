CREATE TABLE USER_SESSIONS
(
    id                 BIGINT PRIMARY KEY,
    user_id            BIGINT       NOT NULL,
    refresh_token_hash VARCHAR(255) NOT NULL UNIQUE,
    device_info        TEXT,
    expires_at         TIMESTAMP    NOT NULL,
    created_at         TIMESTAMP    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES USERS (id)
);

CREATE SEQUENCE user_sessions_seq START WITH 1 INCREMENT BY 1;
