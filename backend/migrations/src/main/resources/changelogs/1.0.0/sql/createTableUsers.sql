CREATE TABLE users
(
    id             BIGINT       NOT NULL PRIMARY KEY,
    email          VARCHAR(255) NOT NULL UNIQUE,
    name           VARCHAR(150) NOT NULL,
    role           user_role    NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    email_verified BOOLEAN      NOT NULL,
    birth_date     DATE         NOT NULL,
    avatar_id      BIGINT       NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL,
    current_xp     BIGINT       NOT NULL,
    level_id       BIGINT       NOT NULL,
    coins          BIGINT       NOT NULL,
    CONSTRAINT fk_users_avatar_id FOREIGN KEY (avatar_id) REFERENCES AVATARS (id),
    CONSTRAINT fk_users_level_id FOREIGN KEY (level_id) REFERENCES LEVELS (id)
);

CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;