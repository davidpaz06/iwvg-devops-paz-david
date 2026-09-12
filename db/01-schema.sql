CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    family_name   VARCHAR(100) NOT NULL,
    email         VARCHAR(150),
    identity      VARCHAR(50),
    address       VARCHAR(200),
    city          VARCHAR(100),
    province      VARCHAR(100),
    postal_code   VARCHAR(20),
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    role          VARCHAR(20) NOT NULL DEFAULT 'USER'
        CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE fractions (
    id            BIGSERIAL PRIMARY KEY,
    numerator     INTEGER NOT NULL,
    denominator   INTEGER NOT NULL,
    user_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_fractions_user_id ON fractions(user_id);
