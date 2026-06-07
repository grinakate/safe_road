-- =====================================================================
-- АВАТАРЫ
-- =====================================================================
-- Очистка таблицы перед заполнением
TRUNCATE TABLE avatars RESTART IDENTITY CASCADE;

INSERT INTO avatars (url, min_level)
VALUES ('/static/avatars/11.png', 1),
       ('/static/avatars/12.png', 2),
       ('/static/avatars/13.png', 2),
       ('/static/avatars/14.png', 3),
       ('/static/avatars/15.png', 4),
       ('/static/avatars/16.png', 5),
       ('/static/avatars/17.png', 6);

-- =====================================================================
-- УРОВНИ
-- =====================================================================
-- Очистка таблицы перед заполнением
TRUNCATE TABLE levels RESTART IDENTITY CASCADE;

INSERT INTO levels (number, title, xp_threshold)
VALUES (1, 'Новичок', 100),
       (2, 'Ученик', 250),
       (3, 'Опытный', 500),
       (4, 'Знаток', 1000),
       (5, 'Профи', 2000);

-- =====================================================================
-- ДОСТИЖЕНИЯ
-- =====================================================================
-- Очистка таблицы перед заполнением
TRUNCATE TABLE achievements RESTART IDENTITY CASCADE;

-- Иконка "Список" (Знаток теории)
-- Школьник прочитал все базовые правила в разделе "Пешеход в городе".
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Знаток теории', 'Прочитай все темы в разделе «Пешеход в городе»', '/static/achievements/list.png',
        '{
          "actionType": "SECTION_COMPLETED",
          "value": 1
        }', 150, true);

-- Иконка "Облако с галочкой" (Словесный патруль)
-- За точные ответы без ошибок.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Словесный патруль', 'Ответь правильно на 15 вопросов подряд без ошибок',
        '/static/achievements/check_bubble.png',
        '{
          "actionType": "ANSWER_STREAK",
          "value": 15
        }', 200, true);

-- Иконка "Сердце" (Верный друг дороги)
-- За регулярность (забота о своей безопасности).
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Верный друг дороги', 'Заходи в приложение 5 дней подряд', '/static/achievements/heart.png',
        '{
          "actionType": "LOGIN_STREAK",
          "value": 5
        }', 300, true);

-- Иконка "Человек в медитации" (Мастер Дзен)
-- За прохождение сложных уровней, где нужно думать.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Мастер Дзен', 'Реши 5 задач повышенной сложности (уровень 3)', '/static/achievements/zen.png',
        '{
          "actionType": "DIFFICULTY_MASTER",
          "value": {
            "level": 3,
            "count": 5
          }
        }', 500, true);

-- Иконка "Компас" (Исследователь города)
-- За изучение всех разделов.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Исследователь города', 'Пройди хотя бы по одному топику в каждом разделе', '/static/achievements/compass.png',
        '{
          "actionType": "EXPLORATION_COMPLETED",
          "value": 1
        }', 400, true);

-- Иконка "Мегафон" (Громкая связь)
-- Про жесты велосипедистов и сигналы.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Громкая связь', 'Пройди топик «Сигналы и маневры» без единой ошибки', '/static/achievements/megaphone.png',
        '{
          "actionType": "TOPIC_PERFECT_RUN",
          "value": 4
        }', 250, true);
-- Предполагаем, что ID топика "Сигналы и маневры" равен 4.

-- Иконка "Кубок" (Главный по дорогам)
-- Финальная ачивка за общий опыт.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Главный по дорогам', 'Набери суммарно 1000 очков опыта', '/static/achievements/trophy.png',
        '{
          "actionType": "TOTAL_XP_REACHED",
          "value": 1000
        }', 1000, true);

-- Иконка "Лампочки/Идеи" (Яркая личность)
-- За изучение темы про экипировку (световозвращатели, шлемы).
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Яркая личность', 'Изучи всё про экипировку и световозвращающие элементы', '/static/achievements/ideas.png',
        '{
          "actionType": "TOPIC_COMPLETED",
          "value": 3
        }', 200, true);
-- Предполагаем, что ID топика "Подготовка к поездке" равен 3.

INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Первопроходец', 'Участие в бета-тестировании приложения',
        '/static/achievements/beta_tester.png',
        '{
          "actionType": "SPECIAL_STATUS",
          "value": "BETA_TESTER"
        }', 1000, true);

