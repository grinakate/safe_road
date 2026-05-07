CREATE TABLE user_achievements
(
    user_id        BIGINT    NOT NULL,
    achievement_id INTEGER   NOT NULL,
    earned_at      TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, achievement_id),
    CONSTRAINT fk_user_achievements_achievement FOREIGN KEY (achievement_id) REFERENCES achievements (id)
);

