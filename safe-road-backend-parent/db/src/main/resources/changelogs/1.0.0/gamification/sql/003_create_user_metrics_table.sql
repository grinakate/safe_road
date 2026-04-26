CREATE TABLE user_metrics
(
    user_id     BIGINT    NOT NULL,
    metric_code TEXT      NOT NULL,
    value       BIGINT    NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, metric_code)
);

