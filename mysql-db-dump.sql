-- None of the below queries are used to create tables. Tables are created automatically by spring boot.
-- Only need is to run the program for creating tables.

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    user_name VARCHAR(255) NOT NULL,
    level INT NOT NULL,
    coins INT NOT NULL,
    country VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    in_tournament BOOLEAN NOT NULL,
    reward_claimed BOOLEAN NOT NULL
);

CREATE TABLE tournament (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_name VARCHAR(255) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL
);

CREATE TABLE tournament_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL,
    competition_started BOOLEAN NOT NULL,
    FOREIGN KEY (tournament_id) REFERENCES tournament(id)
);


CREATE TABLE user_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    score INT NOT NULL,
    active BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (group_id) REFERENCES tournament_group(id)
);