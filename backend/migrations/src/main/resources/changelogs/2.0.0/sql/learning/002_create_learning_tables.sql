CREATE TABLE IF NOT EXISTS user_topic_progress (
    user_id BIGINT NOT NULL,
    topic_id INTEGER NOT NULL,
    status progress_status NOT NULL,
    PRIMARY KEY (user_id, topic_id)
);
CREATE TABLE IF NOT EXISTS user_section_progress (
    user_id BIGINT NOT NULL,
    section_id INTEGER NOT NULL,
    status progress_status NOT NULL,
    PRIMARY KEY (user_id, section_id)
);
CREATE TABLE IF NOT EXISTS user_question_stats (
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    topic_id INTEGER NOT NULL,
    success_streak INTEGER NOT NULL,
    last_result_correct BOOLEAN NOT NULL,
    next_review_at TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, question_id)
);
CREATE TABLE IF NOT EXISTS review_intervals (
    streak_level INTEGER PRIMARY KEY,
    interval_hours INTEGER NOT NULL
);
