INSERT INTO avatars (url) VALUES
    ('https://cdn.safe-road.local/avatars/default-1.png'),
    ('https://cdn.safe-road.local/avatars/default-2.png'),
    ('https://cdn.safe-road.local/avatars/default-3.png')
ON CONFLICT (url) DO NOTHING;
