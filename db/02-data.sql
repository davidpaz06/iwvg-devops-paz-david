INSERT INTO users (id, name, family_name) VALUES
    (1, 'Oscar',   'Fernandez'),
    (2, 'Ana',     'Blanco'),
    (3, 'Oscar',   'López'),
    (4, 'Paula',   'Torres'),
    (5, 'Antonio', 'Blanco'),
    (6, 'David',   'Paz'),
    (7, 'Paula',   'Torres');

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

INSERT INTO fractions (numerator, denominator, user_id) VALUES
    (0, 1, 1), (1, 1, 1), (2, 1, 1),
    (2, 1, 2), (-1, 5, 2), (2, 4, 2), (4, 3, 2),
    (1, 5, 3), (3, -6, 3), (1, 2, 3), (4, 4, 3),
    (2, 2, 4), (4, 4, 4),
    (0, 1, 5), (0, -2, 5), (0, 3, 5),
    (0, 0, 6), (1, 0, 6), (1, 1, 6),
    (0, 0, 7), (1, 0, 7), (1, 1, 7);
