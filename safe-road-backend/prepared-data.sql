-- Prepared test data for integration tests
-- Inserts a user, a section, two topics, a question and a test session
-- IDs chosen to avoid conflicts: users.id = 7, sections.id = 100, topics.id = 200/201, questions.id = 300, test_sessions.id = 400

-- Insert user with id = 7
INSERT INTO users (id, email, password_hash, nickname, birth_date, role, created_at, updated_at, last_login_date)
VALUES (7, 'user7@example.com', 'passhash', 'user-123', '2000-01-01 00:00:00', 'USER', now(), now(), now())
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('users', 'id'), GREATEST((SELECT MAX(id) FROM users), 7));

-- Insert section
INSERT INTO sections (id, title, description, url, order_index, is_active)
VALUES (100, 'S', 'd', 'u', 1, true)
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('sections', 'id'), GREATEST((SELECT MAX(id) FROM sections), 100));

-- Insert topics (T and T2)
INSERT INTO topics (id, section_id, title, content, order_index, is_active)
VALUES (200, 100, 'T', '{}', 1, true)
ON CONFLICT (id) DO NOTHING;
INSERT INTO topics (id, section_id, title, content, order_index, is_active)
VALUES (201, 100, 'T2', '{}', 2, true)
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('topics', 'id'), GREATEST((SELECT MAX(id) FROM topics), 201));

-- Insert question for topic 200
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

-- Insert test session for user 7 and topic 200
INSERT INTO test_sessions (id, user_id, topic_id, section_id, mode, status, questions_data, total_questions,
                           correct_count, created_at)
VALUES (400, 7, 200, NULL, 'TOPIC', 'IN_PROGRESS', '{
  "questionIds": [
    300
  ]
}', 1, 0, now())
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('test_sessions', 'id'), GREATEST((SELECT MAX(id) FROM test_sessions), 400));

-- End of prepared data

