CREATE TABLE user_secrets (
    user_id BIGINT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_users(id)
);


ALTER TABLE app_users
DROP COLUMN email;

ALTER TABLE app_users
DROP COLUMN password;