CREATE TABLE user_question_stats
(
    user_id             BIGINT    NOT NULL,
    question_id         BIGINT    NOT NULL,
    topic_id            INTEGER   NOT NULL,
    success_streak      INTEGER   NOT NULL,
    last_result_correct BOOLEAN   NOT NULL,
    next_review_at      TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, question_id)
);
