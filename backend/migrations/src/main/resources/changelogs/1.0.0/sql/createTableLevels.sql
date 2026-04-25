CREATE TABLE levels
(
    id           BIGINT PRIMARY KEY,
    number       INT         NOT NULL UNIQUE, -- Номер уровня (1, 2, 3...)
    title        VARCHAR(50) NOT NULL,        -- Название уровня (например, "Новичок", "Опытный")
    xp_threshold INT         NOT NULL         -- Количество XP, необходимое для достижения этого уровня
);

INSERT INTO levels (id, number, title, xp_threshold)
VALUES (1, 1, 'Новичок', 0),
       (2, 2, 'Ученик', 100),
       (3, 3, 'Опытный', 250),
       (4, 4, 'Знаток', 500),
       (5, 5, 'Профи', 1000);