-- =========================================================
-- MIGRATION: Add role support to users table
-- =========================================================

-- 1. Create the enum type
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

-- 2. Add the column to users, defaulting existing rows to USER
ALTER TABLE users
    ADD COLUMN role user_role NOT NULL DEFAULT 'USER';
