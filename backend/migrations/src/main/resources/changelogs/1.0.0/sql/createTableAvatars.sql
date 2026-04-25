CREATE TABLE avatars
(
    id        BIGINT PRIMARY KEY,
    name      VARCHAR(50) NOT NULL UNIQUE,
    image_url TEXT        NOT NULL
);

INSERT INTO AVATARS (id, name, image_url)
VALUES (1, 'Медведь', '/assets/avatars/bear.png'),
       (2, 'Сова', '/assets/avatars/owl.png'),
       (3, 'Машина', '/assets/avatars/car.png'),
       (4, 'Шлем', '/assets/avatars/helmet.png'),
       (5, 'Пешеход', '/assets/avatars/pedestrian.png');