CREATE TABLE USER_VERIFICATION_TOKENS
(
    id         BIGINT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES USERS (id)
);

CREATE SEQUENCE users_verification_tokens_seq START WITH 1 INCREMENT BY 1;
