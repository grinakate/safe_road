INSERT INTO review_intervals (streak_level, interval_hours) VALUES
    (1, 24),
    (2, 72),
    (3, 168),
    (4, 336),
    (5, 720)
ON CONFLICT (streak_level) DO NOTHING;
