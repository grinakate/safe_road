-- User used by tests (id = 7)
INSERT INTO users (id, email, password_hash, nickname, birth_date, role, created_at, updated_at,
                   last_login_date)
VALUES (7, 'test@test.ru', '$2a$10$.UvkdVQu7nn3rb.M4h1kreiTMS1tw203NvIxYigSg2lrdFOtCuJw.', 'ТестТестович',
        '2010-01-04 00:00:00.000000', 'USER', '2026-05-24 22:39:21.651272', '2026-05-24 22:39:21.651272',
        '2026-05-24 22:39:21.651272')
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('users', 'id'), GREATEST((SELECT MAX(id) FROM users), 7));

-- Insert section and content used by learning tests
INSERT INTO sections (id, title, description, url, order_index, is_active)
VALUES (100, 'S', 'd', 'u', 1, true)
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('sections', 'id'), GREATEST((SELECT MAX(id) FROM sections), 100));

-- Topics (T and T2)
INSERT INTO topics (id, section_id, title, content, order_index, is_active)
VALUES (200, 100, 'T', '{}', 1, true)
ON CONFLICT (id) DO NOTHING;
INSERT INTO topics (id, section_id, title, content, order_index, is_active)
VALUES (201, 100, 'T2', '{}', 2, true)
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('topics', 'id'), GREATEST((SELECT MAX(id) FROM topics), 201));

-- Question for topic 200
INSERT INTO questions (id, topic_id, type, difficulty_level, content)
VALUES (300, 200, 'CHOICE', 1, '{
  "question_text": "Q?",
  "options": [
    {
      "number": 1,
      "text": "ok",
      "isCorrect": true
    }
  ]
}')
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('questions', 'id'), GREATEST((SELECT MAX(id) FROM questions), 300));

-- Test session for user 7 and topic 200
INSERT INTO test_sessions (id, user_id, topic_id, section_id, mode, status, questions_data, total_questions,
                           correct_count, created_at)
VALUES (400, 7, 200, NULL, 'TOPIC', 'IN_PROGRESS', '{
  "questionIds": [
    300
  ]
}', 1, 0, now())
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('test_sessions', 'id'), GREATEST((SELECT MAX(id) FROM test_sessions), 400));


INSERT INTO user_topic_progress(user_id, topic_id, status)
VALUES (7, 200, 'IN_PROGRESS'),
       (7, 201, 'LOCKED');

-- Gamification seeds: levels, avatars, game_profile for user 7
INSERT INTO levels (id, title, number, xp_threshold)
VALUES (1, 'Novice', 1, 0)
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('levels', 'id'), GREATEST((SELECT MAX(id) FROM levels), 1));

INSERT INTO avatars (id, url, min_level)
VALUES (10, '/avatars/1.png', 1)
ON CONFLICT (id) DO NOTHING;
INSERT INTO avatars (id, url, min_level)
VALUES (11, '/avatars/2.png', 2)
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('avatars', 'id'), GREATEST((SELECT MAX(id) FROM avatars), 11));

INSERT INTO game_profiles (user_id, level_id, avatar_id, current_xp, current_streak, total_active_days,
                           is_leaderboard_participant)
VALUES (7, 1, 10, 0, 0, 0, false)
ON CONFLICT (user_id) DO NOTHING;

