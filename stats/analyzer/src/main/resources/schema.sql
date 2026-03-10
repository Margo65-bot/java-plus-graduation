CREATE SCHEMA IF NOT EXISTS stats_service;

CREATE TABLE IF NOT EXISTS stats_service.user_action (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    action_type VARCHAR NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS stats_service.event_similarity (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_a BIGINT NOT NULL,
    event_b BIGINT NOT NULL,
    score DOUBLE PRECISION NOT NULL
);