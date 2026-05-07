CREATE TABLE user_topic_progress
(
    user_id  BIGINT          NOT NULL,
    topic_id INTEGER         NOT NULL,
    status   progress_status NOT NULL,
    PRIMARY KEY (user_id, topic_id)
);
