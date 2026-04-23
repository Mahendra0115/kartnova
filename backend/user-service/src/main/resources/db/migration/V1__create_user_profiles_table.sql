CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGSERIAL PRIMARY KEY,
    auth_user_id BIGINT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20),
    account_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

ALTER TABLE user_profiles
    ADD CONSTRAINT uk_user_profiles_auth_user_id UNIQUE (auth_user_id);

ALTER TABLE user_profiles
    ADD CONSTRAINT uk_user_profiles_email UNIQUE (email);