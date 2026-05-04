-- =========================================================
-- DATABASE SCHEMA (WITH CASCADE BEST PRACTICES)
-- =========================================================

-- ENUM TYPES
CREATE TYPE content_type AS ENUM ('MOVIE','SHOW');
CREATE TYPE personnel_role AS ENUM ('ACTOR','DIRECTOR');
CREATE TYPE subscription_status AS ENUM ('ACTIVE','PAUSED','CANCELLED','EXPIRED');
CREATE TYPE payment_status AS ENUM ('PENDING','PAID','FAILED','REFUNDED');
CREATE TYPE payment_type AS ENUM ('CARD','MOBILEPAY');

-- ACCOUNTS
CREATE TABLE accounts (
    account_id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    phonenumber VARCHAR(20),
    mail VARCHAR(254) UNIQUE NOT NULL
);

-- USERS
CREATE TABLE users (
    users_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    accounts_account_id BIGINT NOT NULL
        REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- PROFILE
CREATE TABLE profile (
    profile_id BIGSERIAL PRIMARY KEY,
    profilename VARCHAR(45) NOT NULL,
    accounts_account_id BIGINT NOT NULL
        REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- LISTS
CREATE TABLE lists (
    list_id BIGSERIAL PRIMARY KEY,
    listname VARCHAR(80) NOT NULL,
    profile_profile_id BIGINT
        REFERENCES profile(profile_id) ON DELETE CASCADE
);

-- CONTENT
CREATE TABLE content (
    content_id BIGSERIAL PRIMARY KEY,
    originaltitle VARCHAR(200) NOT NULL,
    title VARCHAR(200),
    description TEXT,
    rating DECIMAL,
    releasedate DATE,
    thumbnail VARCHAR(500),
    type content_type NOT NULL
);

-- MOVIE
CREATE TABLE movie (
    movie_id BIGSERIAL PRIMARY KEY,
    duration SMALLINT NOT NULL,
    content_content_id BIGINT NOT NULL
        REFERENCES content(content_id) ON DELETE CASCADE
);

-- SHOWS
CREATE TABLE shows (
    shows_id BIGSERIAL PRIMARY KEY,
    content_content_id BIGINT NOT NULL
        REFERENCES content(content_id) ON DELETE CASCADE
);

-- SEASON
CREATE TABLE season (
    season_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    releasedate DATE,
    shows_shows_id BIGINT
        REFERENCES shows(shows_id) ON DELETE CASCADE
);

-- EPISODE
CREATE TABLE episode (
    episode_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    releasedate DATE,
    duration SMALLINT,
    season_season_id BIGINT
        REFERENCES season(season_id) ON DELETE CASCADE
);

-- REVIEW
CREATE TABLE review (
    review_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(60) NOT NULL,
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 10),
    comment VARCHAR(500) NOT NULL,
    created_at TIMESTAMP,
    profile_profile_id BIGINT
        REFERENCES profile(profile_id) ON DELETE CASCADE,
    content_content_id BIGINT
        REFERENCES content(content_id) ON DELETE CASCADE,
    CONSTRAINT unique_profile_content
        UNIQUE (profile_profile_id, content_content_id)
);

-- GENRE
CREATE TABLE genre (
    genre_id BIGSERIAL PRIMARY KEY,
    genrename VARCHAR(40) UNIQUE NOT NULL
);

-- PERSONNEL
CREATE TABLE personnel (
    personnel_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    roletype personnel_role NOT NULL
);

-- PLAN
CREATE TABLE plan (
    plan_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(40) UNIQUE,
    description TEXT,
    price DECIMAL(10,2),
    currency CHAR(3),
    active BOOLEAN DEFAULT TRUE
);

-- SUBSCRIPTION
CREATE TABLE subscription (
    subscription_id BIGSERIAL PRIMARY KEY,
    startdate DATE,
    enddate DATE,
    next_bill_date DATE,
    status subscription_status,
    accounts_account_id BIGINT
        REFERENCES accounts(account_id) ON DELETE CASCADE,
    plan_plan_id BIGINT
        REFERENCES plan(plan_id)
);

-- PAYMENT METHOD
CREATE TABLE paymentmethod (
    paymentmethod_id BIGSERIAL PRIMARY KEY,
    card_number VARCHAR(25),
    expiration_month SMALLINT,
    expiration_year SMALLINT,
    cvc VARCHAR(4),
    type payment_type,
    default_paymentmethod BOOLEAN,
    accounts_account_id BIGINT
        REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- PAYMENT
CREATE TABLE payment (
    payment_id BIGSERIAL PRIMARY KEY,
    price DECIMAL(10,2),
    currency CHAR(3),
    created_at TIMESTAMP,
    status payment_status,
    subscription_subscription_id BIGINT
        REFERENCES subscription(subscription_id) ON DELETE SET NULL,
    paymentmethod_paymentmethod_id BIGINT
        REFERENCES paymentmethod(paymentmethod_id) ON DELETE SET NULL
);

-- RECEIPT
CREATE TABLE receipt (
    receipt_id BIGSERIAL PRIMARY KEY,
    receipt_number VARCHAR(40) UNIQUE,
    price DECIMAL(10,2),
    paydate TIMESTAMP,
    payment_payment_id BIGINT
        REFERENCES payment(payment_id) ON DELETE CASCADE
);

-- MANY-TO-MANY RELATIONS
CREATE TABLE content_has_list (
    content_content_id BIGINT
        REFERENCES content(content_id) ON DELETE CASCADE,
    lists_list_id BIGINT
        REFERENCES lists(list_id) ON DELETE CASCADE,
    PRIMARY KEY(content_content_id, lists_list_id)
);

CREATE TABLE genre_has_content (
    genre_genre_id BIGINT
        REFERENCES genre(genre_id) ON DELETE CASCADE,
    content_content_id BIGINT
        REFERENCES content(content_id) ON DELETE CASCADE,
    PRIMARY KEY(genre_genre_id, content_content_id)
);

CREATE TABLE content_has_personnel (
    content_content_id BIGINT
        REFERENCES content(content_id) ON DELETE CASCADE,
    personnel_personnel_id BIGINT
        REFERENCES personnel(personnel_id) ON DELETE CASCADE,
    PRIMARY KEY(content_content_id, personnel_personnel_id)
);
