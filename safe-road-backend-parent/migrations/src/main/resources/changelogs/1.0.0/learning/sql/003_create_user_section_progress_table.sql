CREATE TABLE user_section_progress
(
    user_id    BIGINT          NOT NULL,
    section_id INTEGER         NOT NULL,
    status     progress_status NOT NULL,
    PRIMARY KEY (user_id, section_id)
);
