INSERT INTO levels (name, number, xp_threshold) VALUES
    ('Beginner', 1, 0),
    ('Explorer', 2, 100),
    ('Navigator', 3, 250),
    ('Guardian', 4, 500)
ON CONFLICT (number) DO NOTHING;
