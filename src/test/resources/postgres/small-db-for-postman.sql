-- =========================================================
-- TESTCONTAINERS LIMITED DATABASE INIT
-- Combines schema with a trimmed seed for Postman/Newman tests.
-- Seed limits: 20 movies, 20 shows, 20 seasons, 20 episodes,
-- 20 genres, 20 personnel rows, and filtered relationship rows.
-- =========================================================

-- =========================================================
-- DATABASE SCHEMA (WITH CASCADE BEST PRACTICES + USER ROLES)
-- =========================================================

-- ENUM TYPES
CREATE TYPE content_type AS ENUM ('MOVIE','SHOW');
CREATE TYPE personnel_role AS ENUM ('ACTOR','DIRECTOR');
CREATE TYPE subscription_status AS ENUM ('ACTIVE','PAUSED','CANCELLED','EXPIRED');
CREATE TYPE payment_status AS ENUM ('PENDING','PAID','FAILED','REFUNDED');
CREATE TYPE payment_type AS ENUM ('CARD','MOBILEPAY');
CREATE TYPE user_role AS ENUM ('USER','ADMIN');

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
    role user_role NOT NULL DEFAULT 'USER',
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

-- =========================================================
-- LIMITED SEED DATA
-- =========================================================

BEGIN;

INSERT INTO accounts (account_id, firstname, lastname, phonenumber, mail) VALUES (1, 'Post', 'Test', '12345678', 'posttestuser@example.com');
INSERT INTO users (users_id, username, password, role, accounts_account_id) VALUES (1, 'PostTest', '$2b$12$aqZQ8M3o5UtE4c1FkrKoYuYaNFxYSOAe8CSaNkS5FDAQmurex.7Eq', 'ADMIN', 1);
INSERT INTO accounts (account_id, firstname, lastname, phonenumber, mail) VALUES (2, 'Test', 'Sub', '99999999', 'testsub@example.com');
INSERT INTO profile (profile_id, profilename, accounts_account_id) VALUES
(1, 'Umair', 1),
(2, 'Family', 1),
(3, 'Kids', 1);

INSERT INTO lists (list_id, listname, profile_profile_id) VALUES
(1, 'Watchlist', 1),
(2, 'Favorites', 1),
(3, 'Watchlist', 2),
(4, 'Favorites', 2),
(5, 'Watchlist', 3),
(6, 'Favorites', 3);

INSERT INTO plan (plan_id, name, description, price, currency, active) VALUES
(1, 'STANDARD', 'Standard plan', 99.00, 'DKK', TRUE),
(2, 'PREMIUM',  'Premium plan', 999.00, 'DKK', TRUE);

INSERT INTO subscription (subscription_id, startdate, enddate, next_bill_date, status, accounts_account_id, plan_plan_id) VALUES
(1, CURRENT_DATE - 20, CURRENT_DATE + 10, CURRENT_DATE + 10, 'ACTIVE', 1, 1);

INSERT INTO paymentmethod (paymentmethod_id, card_number, expiration_month, expiration_year, cvc, type, default_paymentmethod, accounts_account_id) VALUES
(1, '4242 4242 4242 4242', 12, 2030, '123', 'CARD', TRUE, 1),
(2, NULL, NULL, NULL, NULL, 'MOBILEPAY', FALSE, 1);

INSERT INTO payment (payment_id, price, currency, created_at, status, subscription_subscription_id, paymentmethod_paymentmethod_id) VALUES
(1, 99.00, 'DKK', NOW() - INTERVAL '20 days', 'PAID', 1, 1),
(2, 99.00, 'DKK', NOW() - INTERVAL '1 days',  'FAILED', 1, 2);

INSERT INTO receipt (receipt_id, receipt_number, price, paydate, payment_payment_id) VALUES
(1, '123456789123', 99.00, NOW() - INTERVAL '20 days', 1);

INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (1, 'The Shawshank Redemption', 'The Shawshank Redemption', 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', 9.3, '1994-01-01', 'https://m.media-amazon.com/images/M/MV5BMDFkYTc0MGEtZmNhMC00ZDIzLWFmNTEtODM1ZmRlYWMwMWFmXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (1, 142, 1);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (2, 'The Godfather', 'The Godfather', 'The aging patriarch of an organized crime dynasty in postwar New York City transfers control of his clandestine empire to his reluctant youngest son.', 9.2, '1972-01-01', 'https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (2, 175, 2);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (3, 'The Dark Knight', 'The Dark Knight', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', 9.1, '2008-01-01', 'https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (3, 152, 3);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (4, 'The Godfather: Part II', 'The Godfather: Part II', 'The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate.', 9.0, '1974-01-01', 'https://m.media-amazon.com/images/M/MV5BMWMwMGQzZTItY2JlNC00OWZiLWIyMDctNDk2ZDQ2YjRjMWQ0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (4, 202, 4);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (5, '12 Angry Men', '12 Angry Men', 'The jury in a New York City murder trial is frustrated by a single member whose skeptical caution forces them to more carefully consider the evidence before jumping to a hasty verdict.', 9.0, '1957-01-01', 'https://m.media-amazon.com/images/M/MV5BMWU4N2FjNzYtNTVkNC00NzQ0LTg0MjAtYTJlMjFhNGUxZDFmXkEyXkFqcGdeQXVyNjc1NTYyMjg@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (5, 96, 5);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (6, 'Schindler''s List', 'Schindler''s List', 'In German-occupied Poland during World War II, industrialist Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazis.', 8.9, '1993-01-01', 'https://m.media-amazon.com/images/M/MV5BNDE4OTMxMTctNmRhYy00NWE2LTg3YzItYTk3M2UwOTU5Njg4XkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (6, 195, 6);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (7, 'The Lord of the Rings: The Return of the King', 'The Lord of the Rings: The Return of the King', 'Gandalf and Aragorn lead the World of Men against Sauron''s army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring.', 9.0, '2003-01-01', 'https://m.media-amazon.com/images/M/MV5BNzA5ZDNlZWMtM2NhNS00NDJjLTk4NDItYTRmY2EwMWZlMTY3XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (7, 201, 7);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (8, 'Pulp Fiction', 'Pulp Fiction', 'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', 8.9, '1994-01-01', 'https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (8, 154, 8);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (9, 'The Lord of the Rings: The Fellowship of the Ring', 'The Lord of the Rings: The Fellowship of the Ring', 'A meek Hobbit from the Shire and eight companions set out on a journey to destroy the powerful One Ring and save Middle-earth from the Dark Lord Sauron.', 8.8, '2001-01-01', 'https://m.media-amazon.com/images/M/MV5BN2EyZjM3NzUtNWUzMi00MTgxLWI0NTctMzY4M2VlOTdjZWRiXkEyXkFqcGdeQXVyNDUzOTQ5MjY@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (9, 178, 9);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (10, 'The Good, the Bad and the Ugly', 'The Good, the Bad and the Ugly', 'A bounty hunting scam joins two men in an uneasy alliance against a third in a race to find a fortune in gold buried in a remote cemetery.', 8.8, '1966-01-01', 'https://m.media-amazon.com/images/M/MV5BNjJlYmNkZGItM2NhYy00MjlmLTk5NmQtNjg1NmM2ODU4OTMwXkEyXkFqcGdeQXVyMjUzOTY1NTc@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (10, 178, 10);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (11, 'Forrest Gump', 'Forrest Gump', 'The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweeth', 8.8, '1994-01-01', 'https://m.media-amazon.com/images/M/MV5BNWIwODRlZTUtY2U3ZS00Yzg1LWJhNzYtMmZiYmEyNmU1NjMzXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (11, 142, 11);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (12, 'Fight Club', 'Fight Club', 'An insomniac office worker and a devil-may-care soap maker form an underground fight club that evolves into much more.', 8.8, '1999-01-01', 'https://m.media-amazon.com/images/M/MV5BNDIzNDU0YzEtYzE5Ni00ZjlkLTk5ZjgtNjM3NWE4YzA3Nzk3XkEyXkFqcGdeQXVyMjUzOTY1NTc@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (12, 139, 12);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (13, 'Inception', 'Inception', 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster.', 8.8, '2010-01-01', 'https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (13, 148, 13);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (14, 'The Lord of the Rings: The Two Towers', 'The Lord of the Rings: The Two Towers', 'While Frodo and Sam edge closer to Mordor with the help of the shifty Gollum, the divided fellowship makes a stand against Sauron''s new ally, Saruman, and his hordes of Isengard.', 8.7, '2002-01-01', 'https://m.media-amazon.com/images/M/MV5BZGMxZTdjZmYtMmE2Ni00ZTdkLWI5NTgtNjlmMjBiNzU2MmI5XkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (14, 179, 14);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (15, 'Star Wars: Episode V - The Empire Strikes Back', 'Star Wars: Episode V - The Empire Strikes Back', 'After the Rebels are brutally overpowered by the Empire on the ice planet Hoth, Luke Skywalker begins Jedi training with Yoda, while his friends are pursued across the galaxy by Darth Vader and bounty hunter Boba Fett.', 8.7, '1980-01-01', 'https://m.media-amazon.com/images/M/MV5BYmU1NDRjNDgtMzhiMi00NjZmLTg5NGItZDNiZjU5NTU4OTE0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (15, 124, 15);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (16, 'The Matrix', 'The Matrix', 'When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence.', 8.7, '1999-01-01', 'https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (16, 136, 16);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (17, 'Goodfellas', 'Goodfellas', 'The story of Henry Hill and his life in the mob, covering his relationship with his wife Karen Hill and his mob partners Jimmy Conway and Tommy DeVito in the Italian-American crime syndicate.', 8.7, '1990-01-01', 'https://m.media-amazon.com/images/M/MV5BY2NkZjEzMDgtN2RjYy00YzM1LWI4ZmQtMjIwYjFjNmI3ZGEwXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (17, 145, 17);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (18, 'One Flew Over the Cuckoo''s Nest', 'One Flew Over the Cuckoo''s Nest', 'A criminal pleads insanity and is admitted to a mental institution, where he rebels against the oppressive nurse and rallies up the scared patients.', 8.7, '1975-01-01', 'https://m.media-amazon.com/images/M/MV5BZjA0OWVhOTAtYWQxNi00YzNhLWI4ZjYtNjFjZTEyYjJlNDVlL2ltYWdlL2ltYWdlXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (18, 133, 18);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (19, 'Se7en', 'Se7en', 'Two detectives, a rookie and a veteran, hunt a serial killer who uses the seven deadly sins as his motives.', 8.6, '1995-01-01', 'https://m.media-amazon.com/images/M/MV5BOTUwODM5MTctZjczMi00OTk4LTg3NWUtNmVhMTAzNTNjYjcyXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (19, 127, 19);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (20, 'Seven Samurai', 'Seven Samurai', 'A poor village under attack by bandits recruits seven unemployed samurai to help them defend themselves.', 8.6, '1954-01-01', 'https://m.media-amazon.com/images/M/MV5BOWE4ZDdhNmMtNzE5ZC00NzExLTlhNGMtY2ZhYjYzODEzODA1XkEyXkFqcGdeQXVyNTAyODkwOQ@@._V1_SX300.jpg', 'MOVIE');
INSERT INTO movie (movie_id, duration, content_content_id) VALUES (20, 207, 20);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (101, 'Under the Dome', 'Under the Dome', 'Under the Dome is the story of a small town that is suddenly and inexplicably sealed off from the rest of the world by an enormous transparent dome. The town''s inhabitants must deal with surviving the post-apocalyptic conditions while searching for answers about the dome, where it came from and if and when it will go away.', 6.6, '2013-06-24', 'https://static.tvmaze.com/uploads/images/original_untouched/610/1525272.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (1, 101);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (1, 'Season 1', '2013-06-24', 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (1, 'Episode 1', 'Seeded episode', '2013-06-24', 31, 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (2, 'Episode 2', 'Seeded episode', '2013-06-24', 32, 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (3, 'Episode 3', 'Seeded episode', '2013-06-24', 33, 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (4, 'Episode 4', 'Seeded episode', '2013-06-24', 34, 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (5, 'Episode 5', 'Seeded episode', '2013-06-24', 35, 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (6, 'Episode 6', 'Seeded episode', '2013-06-24', 36, 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (7, 'Episode 7', 'Seeded episode', '2013-06-24', 37, 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (8, 'Episode 8', 'Seeded episode', '2013-06-24', 38, 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (9, 'Episode 9', 'Seeded episode', '2013-06-24', 39, 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (10, 'Episode 10', 'Seeded episode', '2013-06-24', 40, 1);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (2, 'Season 2', '2013-06-24', 1);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (11, 'Episode 1', 'Seeded episode', '2013-06-24', 31, 2);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (12, 'Episode 2', 'Seeded episode', '2013-06-24', 32, 2);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (13, 'Episode 3', 'Seeded episode', '2013-06-24', 33, 2);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (14, 'Episode 4', 'Seeded episode', '2013-06-24', 34, 2);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (15, 'Episode 5', 'Seeded episode', '2013-06-24', 35, 2);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (16, 'Episode 6', 'Seeded episode', '2013-06-24', 36, 2);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (17, 'Episode 7', 'Seeded episode', '2013-06-24', 37, 2);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (18, 'Episode 8', 'Seeded episode', '2013-06-24', 38, 2);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (19, 'Episode 9', 'Seeded episode', '2013-06-24', 39, 2);
INSERT INTO episode (episode_id, title, description, releasedate, duration, season_season_id) VALUES (20, 'Episode 10', 'Seeded episode', '2013-06-24', 40, 2);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (102, 'Person of Interest', 'Person of Interest', 'You are being watched. The government has a secret system, a machine that spies on you every hour of every day. I know because I built it. I designed the Machine to detect acts of terror but it sees everything. Violent crimes involving ordinary people. People like you. Crimes the government considered "irrelevant". They wouldn''t act so I decided I would. But I needed a partner. Someone with the skills to intervene. Hunted by the authorities, we work in secret. You''ll never find us. But victim or perpetrator, if your number is up, we''ll find you.', 8.8, '2011-09-22', 'https://static.tvmaze.com/uploads/images/original_untouched/163/407679.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (2, 102);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (3, 'Season 1', '2011-09-22', 2);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (4, 'Season 2', '2011-09-22', 2);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (103, 'Bitten', 'Bitten', 'Based on the critically acclaimed series of novels from Kelley Armstrong. Set in Toronto and upper New York State, Bitten follows the adventures of 28-year-old Elena Michaels, the world''s only female werewolf. An orphan, Elena thought she finally found her "happily ever after" with her new love Clayton, until her life changed forever. With one small bite, the normal life she craved was taken away and she was left to survive life with the Pack.', 7.4, '2014-01-11', 'https://static.tvmaze.com/uploads/images/original_untouched/0/15.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (3, 103);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (5, 'Season 1', '2014-01-11', 3);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (6, 'Season 2', '2014-01-11', 3);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (104, 'Arrow', 'Arrow', 'After a violent shipwreck, billionaire playboy Oliver Queen was missing and presumed dead for five years before being discovered alive on a remote island in the Pacific. He returned home to Starling City, welcomed by his devoted mother Moira, beloved sister Thea and former flame Laurel Lance. With the aid of his trusted chauffeur/bodyguard John Diggle, the computer-hacking skills of Felicity Smoak and the occasional, reluctant assistance of former police detective, now beat cop, Quentin Lance, Oliver has been waging a one-man war on crime.', 7.4, '2012-10-10', 'https://static.tvmaze.com/uploads/images/original_untouched/143/358967.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (4, 104);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (7, 'Season 1', '2012-10-10', 4);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (8, 'Season 2', '2012-10-10', 4);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (105, 'True Detective', 'True Detective', 'Touch darkness and darkness touches you back. True Detective centers on troubled cops and the investigations that drive them to the edge. Each season features a new cast and a new case.True Detective is an American anthology crime drama television series created and written by Nic Pizzolatto.', 8.1, '2014-01-12', 'https://static.tvmaze.com/uploads/images/original_untouched/490/1226764.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (5, 105);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (9, 'Season 1', '2014-01-12', 5);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (10, 'Season 2', '2014-01-12', 5);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (106, 'The 100', 'The 100', 'Ninety-seven years ago, nuclear Armageddon decimated planet Earth, destroying civilization. The only survivors were the 400 inhabitants of 12 international space stations that were in orbit at the time. Three generations have been born in space, the survivors now number 4,000, and resources are running out on their dying "Ark" - the 12 stations now linked together and repurposed to keep the survivors alive. Draconian measures including capital punishment and population control are the order of the day, as the leaders of the Ark take ruthless steps to ensure their future, including secretly exiling a group of 100 juvenile prisoners to the Earth''s surface to test whether it''s habitable.', 7.7, '2014-03-19', 'https://static.tvmaze.com/uploads/images/original_untouched/477/1194981.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (6, 106);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (11, 'Season 1', '2014-03-19', 6);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (12, 'Season 2', '2014-03-19', 6);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (107, 'Homeland', 'Homeland', 'The winner of 6 Emmy Awards including Outstanding Drama Series, Homeland is an edge-of-your-seat sensation. Marine Sergeant Nicholas Brody is both a decorated hero and a serious threat. CIA officer Carrie Mathison is tops in her field despite being bipolar. The delicate dance these two complex characters perform, built on lies, suspicion, and desire, is at the heart of this gripping, emotional thriller in which nothing short of the fate of our nation is at stake.', 8.2, '2011-10-02', 'https://static.tvmaze.com/uploads/images/original_untouched/498/1245275.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (7, 107);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (13, 'Season 1', '2011-10-02', 7);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (14, 'Season 2', '2011-10-02', 7);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (108, 'Glee', 'Glee', 'Glee is a musical comedy about a group of ambitious and talented young adults in search of strength, acceptance and, ultimately, their voice.', 6.6, '2009-05-19', 'https://static.tvmaze.com/uploads/images/original_untouched/0/73.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (8, 108);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (15, 'Season 1', '2009-05-19', 8);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (16, 'Season 2', '2009-05-19', 8);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (109, 'Revenge', 'Revenge', 'This is not a story about forgiveness; Revenge is a show about retribution. Meet Emily Thorne, the newest resident of The Hamptons. When she was a little girl (and known as Amanda Clarke) her father, David Clarke, was framed for a horrific crime and subsequently sent to prison. While serving his time, the conspirators plotted and murdered David in order to prevent the truth from coming out. Emily is now back with a new identity and ready to take vengeance on the people that murdered her father and stole her childhood.', 7.7, '2011-09-21', 'https://static.tvmaze.com/uploads/images/original_untouched/82/206879.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (9, 109);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (17, 'Season 1', '2011-09-21', 9);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (18, 'Season 2', '2011-09-21', 9);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (110, 'Grimm', 'Grimm', 'Grimm is a drama series inspired by the classic Grimm Brothers'' Fairy Tales. After Portland homicide detective Nick Burkhardt discovers he''s descended from an elite line of criminal profilers known as "Grimms", he increasingly finds his responsibilities as a detective at odds with his new responsibilities as a Grimm.', 8.4, '2011-10-28', 'https://static.tvmaze.com/uploads/images/original_untouched/69/174906.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (10, 110);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (19, 'Season 1', '2011-10-28', 10);
INSERT INTO season (season_id, title, releasedate, shows_shows_id) VALUES (20, 'Season 2', '2011-10-28', 10);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (111, 'Gotham', 'Gotham', 'The good. The evil. The beginning.Everyone knows the name Commissioner Gordon. He is one of the crime world''s greatest foes, a man whose reputation is synonymous with law and order. But what is known of Gordon''s story and his rise from rookie detective to Police Commissioner? What did it take to navigate the multiple layers of corruption that secretly ruled Gotham City, the spawning ground of the world''s most iconic villains? And what circumstances created them – the larger-than-life personas who would become Catwoman, The Penguin, The Riddler, Two-Face and The Joker?Gotham is an origin story of the great DC Comics Super-Villains and vigilantes, revealing an entirely new chapter that has never been told. It follows one cop''s rise through a dangerously corrupt city teetering between good and evil, and chronicles the birth of one of the most popular super heroes of our time.', 7.7, '2014-09-22', 'https://static.tvmaze.com/uploads/images/original_untouched/189/474715.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (11, 111);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (112, 'Lost Girl', 'Lost Girl', 'Lost Girl follows supernatural seductress Bo, a Succubus who feeds on the sexual energy of humans. Growing up with human parents, Bo had no reason to believe she was anything other than the girl next door — until she drained her boyfriend to death in their first sexual encounter. Now she has hit the road alone and afraid.  She discovers she is one of the Fae, creatures of legend and folklore, who pass as humans while feeding off them secretly and in different ways, as they have for millennia. Relieved yet horrified to find out that she is not alone, Bo decides to take the middle path between the humans and the Fae while embarking on a personal mission to unlock the secrets of her origin.With the help of her human sidekick, Kenzi, and Dyson, a sexy police detective, Bo takes on a challenge every week helping a Fae or human client who comes to her to solve a mystery, or to right a wrong.', 7.9, '2010-09-12', 'https://static.tvmaze.com/uploads/images/original_untouched/0/137.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (12, 112);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (113, 'The Flash', 'The Flash', 'After a particle accelerator causes a freak storm, CSI Investigator Barry Allen is struck by lightning and falls into a coma. Months later he awakens with the power of super speed, granting him the ability to move through Central City like an unseen guardian angel. Though initially excited by his newfound powers, Barry is shocked to discover he is not the only "meta-human" who was created in the wake of the accelerator explosion -- and not everyone is using their new powers for good. Barry partners with S.T.A.R. Labs and dedicates his life to protect the innocent. For now, only a few close friends and associates know that Barry is literally the fastest man alive, but it won''t be long before the world learns what Barry Allen has become...The Flash!', 7.5, '2014-10-07', 'https://static.tvmaze.com/uploads/images/original_untouched/448/1121792.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (13, 113);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (114, 'Continuum', 'Continuum', 'Continuum centers on the conflict between a group of rebels from the year 2077 who time-travel to Vancouver, BC, in 2012, and a police officer who accidentally accompanies them. In spite of being many years early, the rebel group decides to continue its violent campaign to stop corporations of the future from replacing governments, while the police officer endeavours to stop them without revealing to anyone that she and the rebels are from the future.', 8.0, '2012-05-27', 'https://static.tvmaze.com/uploads/images/original_untouched/0/184.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (14, 114);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (115, 'Constantine', 'Constantine', 'Based on the wildly popular comic book series "Hellblazer" from DC Comics, seasoned demon hunter and master of the occult John Constantine is armed with a ferocious knowledge of the dark arts and a wickedly naughty wit. He fights the good fight - or at least he did. With his soul already damned to hell, he''s decided to abandon his campaign against evil until a series of events thrusts him back into the fray, and he''ll do whatever it takes to protect the innocent. With the balance of good and evil on the line‎, Constantine will use his skills to travel the country, find the supernatural terrors that threaten our world and send them back where they belong. After that, who knows... maybe there''s hope for him and his soul after all.', 7.5, '2014-10-24', 'https://static.tvmaze.com/uploads/images/original_untouched/0/154.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (15, 115);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (116, 'Penny Dreadful', 'Penny Dreadful', 'Some of literature''s most terrifying characters, including Dr. Frankenstein and his monster, Dorian Gray, and iconic figures from the novel Dracula are lurking in the darkest corners of Victorian London. They are joined by a core of original characters in a complex, frightening new narrative. Penny Dreadful is a psychological thriller filled with dark mystery and suspense, where personal demons from the past can be stronger than vampires, evil spirits and immortal beasts.', 8.2, '2014-05-11', 'https://static.tvmaze.com/uploads/images/original_untouched/48/122260.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (16, 116);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (117, 'The Amazing Race', 'The Amazing Race', 'From athletes and actors to tattoo artists, social workers and musicians - a diverse mix of teams will need to utilize their street smarts and savvy know-how to compete in the race of their lifetime.', 6.8, '2001-09-05', 'https://static.tvmaze.com/uploads/images/original_untouched/587/1468637.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (17, 117);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (118, 'Supernatural', 'Supernatural', 'This haunting series follows the Winchester brothers as they crisscross the lonely and mysterious back roads of the country in their ''67 Chevy Impala, hunting down every evil supernatural force they encounter along the way.', 8.3, '2005-09-13', 'https://static.tvmaze.com/uploads/images/original_untouched/445/1114097.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (18, 118);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (119, 'The Strain', 'The Strain', 'The Strain is a high-concept thriller that tells the story of Dr. Ephraim Goodweather, the head of the Center for Disease Control Canary Team in New York City. He and his team are called upon to investigate a mysterious viral outbreak with hallmarks of an ancient and evil strain of vampirism. As the strain spreads, Goodweather, his team, and an assembly of everyday New Yorkers wage war for the fate of humanity itself.', 7.5, '2014-07-13', 'https://static.tvmaze.com/uploads/images/original_untouched/444/1111710.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (19, 119);
INSERT INTO content (content_id, originaltitle, title, description, rating, releasedate, thumbnail, type) VALUES (120, 'The Last Ship', 'The Last Ship', 'Their mission is simple: Find a cure. Stop the virus. Save the world. When a global pandemic wipes out eighty percent of the planet''s population, the crew of a lone and unaffected Navy destroyer, the USS Nathan James, must find a way to pull humanity from the brink of extinction.', 7.5, '2014-06-22', 'https://static.tvmaze.com/uploads/images/original_untouched/164/412464.jpg', 'SHOW');
INSERT INTO shows (shows_id, content_content_id) VALUES (20, 120);
INSERT INTO genre (genre_id, genrename) VALUES (1, 'Drama') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (2, 'Crime') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (3, 'Action') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (4, 'Biography') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (5, 'History') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (6, 'Adventure') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (7, 'Western') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (8, 'Romance') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (9, 'Sci-Fi') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (10, 'Fantasy') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (11, 'Mystery') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (12, 'Family') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (13, 'Thriller') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (14, 'War') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (15, 'Comedy') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (16, 'Animation') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (17, 'Horror') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (18, 'Music') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (19, 'Film-Noir') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre (genre_id, genrename) VALUES (20, 'Musical') ON CONFLICT (genrename) DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 2) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 3) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 3) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 3) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 4) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 4) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 5) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 5) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (4, 6) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 6) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (5, 6) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 7) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (6, 7) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 7) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 8) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 8) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 9) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (6, 9) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 9) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (6, 10) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (7, 10) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 11) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (8, 11) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 12) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 13) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (6, 13) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (9, 13) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 14) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (6, 14) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 14) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 15) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (6, 15) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (10, 15) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 16) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (9, 16) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (4, 17) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 17) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 17) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 18) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 19) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 19) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (11, 19) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 20) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 20) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 101) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (13, 101) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 102) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 102) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 103) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (17, 103) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (8, 103) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 104) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 104) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 105) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 105) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (13, 105) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 106) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (6, 106) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 107) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (13, 107) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 108) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (18, 108) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (8, 108) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 109) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (13, 109) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (11, 109) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 110) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 110) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 111) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 111) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 111) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 112) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (10, 112) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (17, 112) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 113) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 113) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 114) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (2, 114) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 115) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 115) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (17, 115) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (17, 116) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (13, 116) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 117) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (6, 117) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (12, 117) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 118) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 118) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 119) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (17, 119) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (13, 119) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (1, 120) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (3, 120) ON CONFLICT DO NOTHING;
INSERT INTO genre_has_content (genre_genre_id, content_content_id) VALUES (13, 120) ON CONFLICT DO NOTHING;
INSERT INTO personnel (personnel_id, name, roletype) VALUES (1, 'Frank Darabont', 'DIRECTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (2, 'Tim Robbins', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (3, 'Morgan Freeman', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (4, 'Bob Gunton', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (5, 'Francis Ford Coppola', 'DIRECTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (6, 'Marlon Brando', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (7, 'Al Pacino', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (8, 'James Caan', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (9, 'Christopher Nolan', 'DIRECTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (10, 'Christian Bale', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (11, 'Heath Ledger', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (12, 'Aaron Eckhart', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (13, 'Robert De Niro', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (14, 'Robert Duvall', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (15, 'Sidney Lumet', 'DIRECTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (16, 'Henry Fonda', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (17, 'Lee J. Cobb', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (18, 'Martin Balsam', 'ACTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (19, 'Steven Spielberg', 'DIRECTOR');
INSERT INTO personnel (personnel_id, name, roletype) VALUES (20, 'Liam Neeson', 'ACTOR');
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (1, 3) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (1, 4) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (2, 5) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (2, 6) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (2, 7) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (2, 8) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (3, 9) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (3, 10) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (3, 11) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (3, 12) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (4, 5) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (4, 7) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (4, 13) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (4, 14) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (5, 15) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (5, 16) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (5, 17) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (5, 18) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (6, 19) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (6, 20) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (13, 9) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (17, 13) ON CONFLICT DO NOTHING;
INSERT INTO content_has_personnel (content_content_id, personnel_personnel_id) VALUES (19, 3) ON CONFLICT DO NOTHING;

-- CONTENT_HAS_LIST: deterministic limited relationships
INSERT INTO content_has_list (content_content_id, lists_list_id)
SELECT c.content_id, l.list_id
FROM lists l
JOIN content c ON c.content_id IN (1,2,3,4,5,101,102,103,104,105)
ON CONFLICT DO NOTHING;

-- REVIEWS: deterministic limited reviews
INSERT INTO review (title, rating, comment, created_at, profile_profile_id, content_content_id) VALUES
('Amazing', 5, 'Really enjoyed it.', NOW() - INTERVAL '10 days', 1, 1),
('Good', 4, 'Pretty solid overall.', NOW() - INTERVAL '8 days', 1, 2),
('Okay', 3, 'Could be better.', NOW() - INTERVAL '6 days', 2, 101),
('Masterpiece', 5, 'One of the best I have seen.', NOW() - INTERVAL '4 days', 2, 102),
('Not great', 2, 'Not my taste, but fine.', NOW() - INTERVAL '2 days', 3, 3);

-- FIX SEQUENCES
SELECT setval(pg_get_serial_sequence('accounts','account_id'), (SELECT COALESCE(MAX(account_id),1) FROM accounts));
SELECT setval(pg_get_serial_sequence('users','users_id'), (SELECT COALESCE(MAX(users_id),1) FROM users));
SELECT setval(pg_get_serial_sequence('profile','profile_id'), (SELECT COALESCE(MAX(profile_id),1) FROM profile));
SELECT setval(pg_get_serial_sequence('lists','list_id'), (SELECT COALESCE(MAX(list_id),1) FROM lists));
SELECT setval(pg_get_serial_sequence('content','content_id'), (SELECT COALESCE(MAX(content_id),1) FROM content));
SELECT setval(pg_get_serial_sequence('movie','movie_id'), (SELECT COALESCE(MAX(movie_id),1) FROM movie));
SELECT setval(pg_get_serial_sequence('shows','shows_id'), (SELECT COALESCE(MAX(shows_id),1) FROM shows));
SELECT setval(pg_get_serial_sequence('season','season_id'), (SELECT COALESCE(MAX(season_id),1) FROM season));
SELECT setval(pg_get_serial_sequence('episode','episode_id'), (SELECT COALESCE(MAX(episode_id),1) FROM episode));
SELECT setval(pg_get_serial_sequence('genre','genre_id'), (SELECT COALESCE(MAX(genre_id),1) FROM genre));
SELECT setval(pg_get_serial_sequence('personnel','personnel_id'), (SELECT COALESCE(MAX(personnel_id),1) FROM personnel));
SELECT setval(pg_get_serial_sequence('plan','plan_id'), (SELECT COALESCE(MAX(plan_id),1) FROM plan));
SELECT setval(pg_get_serial_sequence('subscription','subscription_id'), (SELECT COALESCE(MAX(subscription_id),1) FROM subscription));
SELECT setval(pg_get_serial_sequence('paymentmethod','paymentmethod_id'), (SELECT COALESCE(MAX(paymentmethod_id),1) FROM paymentmethod));
SELECT setval(pg_get_serial_sequence('payment','payment_id'), (SELECT COALESCE(MAX(payment_id),1) FROM payment));
SELECT setval(pg_get_serial_sequence('receipt','receipt_id'), (SELECT COALESCE(MAX(receipt_id),1) FROM receipt));
SELECT setval(pg_get_serial_sequence('review','review_id'), (SELECT COALESCE(MAX(review_id),1) FROM review));

COMMIT;
