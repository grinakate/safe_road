CREATE TABLE review_intervals
(
    streak_level   INTEGER PRIMARY KEY,
    interval_hours INTEGER NOT NULL
);

INSERT INTO review_intervals (streak_level, interval_hours)
VALUES (1, 24),
       (2, 72),
       (3, 168),
       (4, 336),
       (5, 720);
