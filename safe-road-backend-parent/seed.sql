-- Sections
INSERT INTO sections (id, name, order_index)
VALUES (1, 'Пешеход в городе', 1),
       (2, 'Велосипед и самокат', 2),
       (3, 'Дорожные знаки', 3);

-- Topics
INSERT INTO topics (id, section_id, name, description, order_index, xp_reward)
VALUES (1, 1, 'Светофор и переход', 'Основы перехода дороги', 1, 100),
       (2, 1, 'Невидимые ловушки', 'Опасности на остановках', 2, 100),
       (3, 2, 'Правила для колес', 'Велосипед и самокат', 3, 150),
       (4, 2, 'Экипировка', 'Безопасность в движении', 4, 150),
       (5, 3, 'Важные знаки', 'Учимся читать дорогу', 5, 200);

-- Questions (Пример для одного топика)
INSERT INTO questions (id, topic_id, type, difficulty_level, content)
VALUES (1, 1, 'CHOICE', 1, '{
  "text": "Что нужно сделать, когда загорелся желтый сигнал светофора?",
  "image_url": "https://base.url/yellow_light.png"
}'),
       (2, 1, 'CHOICE', 1, '{
         "text": "Где безопаснее всего переходить дорогу?",
         "image_url": "https://base.url/crosswalk.png"
       }');

-- Answers
INSERT INTO answers (question_id, text, is_correct, feedback)
VALUES (1, 'Сразу бежать через дорогу', false, 'Желтый сигнал предупреждает о смене цвета. Переходить на него нельзя!'),
       (1, 'Остановиться и приготовиться ждать', true, 'Верно! Желтый — значит жди!'),
       (2, 'По зебре или подземному переходу', true, 'Верно! Это самые безопасные места.'),
       (2, 'Там, где нет машин', false, 'Машины могут появиться внезапно. Используй переход!');

-- Добавляем вопросы в таблицу Questions
INSERT INTO questions (id, topic_id, type, difficulty_level, content)
VALUES (3, 1, 'CHOICE', 2, '{
  "text": "Что делать, если зеленый замигал, а ты на середине дороги?"
}'),
       (4, 1, 'CHOICE', 1, '{
         "text": "Зеленый человечек на светофоре мигает. Что это значит?"
       }'),
       (5, 1, 'CHOICE', 3, '{
         "text": "Можно ли переходить на зеленый, если едет скорая с сиреной?"
       }'),
       (6, 2, 'CHOICE', 2, '{
         "text": "Почему опасно переходить дорогу в капюшоне и наушниках?"
       }'),
       (7, 2, 'CHOICE', 2, '{
         "text": "Кто должен уступить при выезде машины из двора?"
       }'),
       (8, 2, 'CHOICE', 1, '{
         "text": "Можно ли играть в мяч рядом с дорогой?"
       }'),
       (9, 3, 'CHOICE', 2, '{
         "text": "Как велосипедист показывает поворот налево?"
       }'),
       (10, 3, 'CHOICE', 3, '{
         "text": "Где можно ехать на велосипеде после 14 лет?"
       }');

-- Добавляем ответы
INSERT INTO answers (question_id, text, is_correct, feedback)
VALUES (3, 'Спокойно дойти до конца или подождать на островке', true, 'Верно! Главное — не метаться.'),
       (3, 'Побежать назад', false, 'Назад бежать опасно, водители тебя не ждут там.'),
       (4, 'Время перехода заканчивается', true, 'Верно! Это предупреждение.'),
       (4, 'Светофор сломался', false, 'Нет, это штатный режим работы.'),
       (5, 'Нет, нужно пропустить спецтранспорт', true, 'Верно! Сирена — главный приоритет.'),
       (5, 'Да, у меня же зеленый', false, 'Правила обязывают пропускать скорую с сиреной в любом случае.'),
       (6, 'Ты не услышишь и не увидишь машину', true, 'Точно! Органы чувств должны быть свободны.'),
       (7, 'Водитель, выезжающий из двора', true, 'Да, тротуар принадлежит пешеходам.'),
       (9, 'Вытянуть левую руку в сторону', true, 'Правильно! Это международный жест.'),
       (10, 'По правому краю проезжей части', true, 'Да, ты становишься полноценным участником движения.');


-- 1. Иконка "Список" (Верхний левый угол)
-- Школьник прочитал все базовые правила.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Знаток теории', 'Прочитай все темы в разделе «Пешеход в городе»', '/static/achievements/list.png',
        '{"type": "section_completion", "section_id": 1}', 150, true);

-- Иконка "Облако с галочкой" (Верхний ряд, центр)
-- За точные ответы без ошибок.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Словесный патруль', 'Ответь правильно на 15 вопросов подряд без ошибок', '/static/achievements/check_bubble.png',
        '{"type": "streak", "count": 15}', 200, true);

-- Иконка "Сердце" (Верхний правый угол)
-- За регулярность (забота о своей безопасности).
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Верный друг дороги', 'Заходи в приложение 5 дней подряд', '/static/achievements/heart.png',
        '{"type": "login_streak", "days": 5}', 300, true);

-- Иконка "Человек в медитации" (Средний ряд, лево)
-- За прохождение сложных уровней, где нужно думать.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Мастер Дзен', 'Реши 5 задач повышенной сложности (уровень 3)', '/static/achievements/zen.png',
        '{"type": "difficulty_completion", "level": 3, "count": 5}', 500, true);

-- Иконка "Компас" (Средний ряд, центр)
-- За изучение всех разделов.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Исследователь города', 'Пройди хотя бы по одному топику в каждом разделе', '/static/achievements/compass.png',
        '{"type": "all_sections_touched", "min_topics": 1}', 400, true);

-- Иконка "Мегафон" (Средний ряд, право)
-- Про жесты велосипедистов и сигналы.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Громкая связь', 'Пройди топик «Правила для колес» без единой ошибки', '/static/achievements/megaphone.png',
        '{"type": "topic_perfect_run", "topic_id": 3}', 250, true);

-- Иконка "Кубок"
-- Финальная ачивка за общий опыт.
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Главный по дорогам', 'Набери суммарно 1000 очков опыта', '/static/achievements/trophy.png',
        '{"type": "total_xp", "target": 1000}', 1000, true);

-- Иконка "Лампочки/Идеи" (Нижний ряд, право)
-- За изучение темы про экипировку (световозвращатели, шлемы).
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES ('Яркая личность', 'Изучи всё про экипировку и световозвращающие элементы', '/static/ideas.png',
        '{"type": "topic_completion", "topic_id": 4}', 200, true);

-- Архивное или сезонное достижение (isActive = false)
INSERT INTO achievements (name, description, icon_url, requirements, reward_xp, is_active)
VALUES('Первопроходец', 'Участие в бета-тестировании приложения',
 '/static/achievements/beta_tester.png',
 '{"type": "beta_user", "value": true}', 1000, false);


-- Очищаем и вставляем заново с новой структурой
UPDATE achievements SET requirements = '{"actionType": "SECTION_COMPLETED", "value": 1}' WHERE id = 10;
UPDATE achievements SET requirements = '{"actionType": "ANSWER_STREAK", "value": 15}' WHERE id = 11;
UPDATE achievements SET requirements = '{"actionType": "LOGIN_STREAK", "value": 5}' WHERE id = 12;
UPDATE achievements SET requirements = '{"actionType": "DIFFICULTY_MASTER", "value": {"level": 3, "count": 5}}' WHERE id = 13;
UPDATE achievements SET requirements = '{"actionType": "EXPLORATION_COMPLETED", "value": 1}' WHERE id = 14;
UPDATE achievements SET requirements = '{"actionType": "TOPIC_PERFECT_RUN", "value": 3}' WHERE id = 15;
UPDATE achievements SET requirements = '{"actionType": "TOTAL_XP_REACHED", "value": 1000}' WHERE id = 7;
UPDATE achievements SET requirements = '{"actionType": "TOPIC_COMPLETED", "value": 4}' WHERE id = 8;
UPDATE achievements SET requirements = '{"actionType": "SPECIAL_STATUS", "value": "BETA_TESTER"}' WHERE id = 9;


