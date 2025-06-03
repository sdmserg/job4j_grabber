CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    text TEXT,
    link TEXT UNIQUE,
    created BIGINT
);