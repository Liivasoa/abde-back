CREATE TABLE book (
    id      BIGINT PRIMARY KEY,
    issued  DATE,
    title   VARCHAR(1024) NOT NULL,
    languages VARCHAR(255),
    subjects  TEXT
);
