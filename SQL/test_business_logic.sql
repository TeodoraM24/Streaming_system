-- =========================================================
-- TEST SCRIPT FOR STREAMING BUSINESS LOGIC
-- Run after:
-- 1. streaming_schema.sql
-- 2. streaming-system-v1-postgres-seed-fixed.sql
-- 3. streaming_business_logic.sql
-- =========================================================

-- =========================================================
-- 1. EXISTENCE CHECKS
-- =========================================================

-- Functions
SELECT routine_name
FROM information_schema.routines
WHERE routine_schema = 'public'
  AND routine_name IN (
    'cleanup_old_receipts',
    'create_receipt_on_paid_payment',
    'find_movies_by_genre',
    'get_average_rating_for_content',
    'process_payment',
    'recalculate_content_rating'
  )
ORDER BY routine_name;

-- Triggers
SELECT trigger_name, event_object_table
FROM information_schema.triggers
WHERE trigger_schema = 'public'
  AND trigger_name IN (
    'trg_payment_create_receipt',
    'trg_review_recalculate_rating_after_change'
  )
ORDER BY trigger_name;

-- View
SELECT table_name
FROM information_schema.views
WHERE table_schema = 'public'
  AND table_name = 'vw_content_average_rating';


-- =========================================================
-- 2. TEST: find_movies_by_genre
-- Should return all movies linked to the genre 'Drama'
-- =========================================================
SELECT * FROM find_movies_by_genre('Drama');

SELECT COUNT(*) AS drama_movie_count
FROM find_movies_by_genre('Drama');


-- =========================================================
-- 3. TEST: get_average_rating_for_content
-- Before adding reviews, this may be NULL if no reviews exist
-- =========================================================
SELECT get_average_rating_for_content(1) AS avg_before_reviews;

INSERT INTO review (title, rating, comment, created_at, profile_profile_id, content_content_id)
VALUES ('Review 1', 4, 'Good', NOW(), 1, 1)
ON CONFLICT (profile_profile_id, content_content_id)
DO UPDATE SET
    title = EXCLUDED.title,
    rating = EXCLUDED.rating,
    comment = EXCLUDED.comment,
    created_at = EXCLUDED.created_at;

INSERT INTO review (title, rating, comment, created_at, profile_profile_id, content_content_id)
VALUES ('Review 2', 5, 'Great', NOW(), 2, 1)
ON CONFLICT (profile_profile_id, content_content_id)
DO UPDATE SET
    title = EXCLUDED.title,
    rating = EXCLUDED.rating,
    comment = EXCLUDED.comment,
    created_at = EXCLUDED.created_at;

SELECT get_average_rating_for_content(1) AS avg_after_two_reviews;


-- =========================================================
-- 4. TEST: trigger recalculates content.rating
-- The trigger should automatically update content.rating
-- =========================================================
SELECT content_id, title, rating
FROM content
WHERE content_id = 1;

INSERT INTO review (title, rating, comment, created_at, profile_profile_id, content_content_id)
VALUES ('Updated review', 3, 'ok', NOW(), 3, 1)
ON CONFLICT (profile_profile_id, content_content_id)
DO UPDATE SET
    title = EXCLUDED.title,
    rating = EXCLUDED.rating,
    comment = EXCLUDED.comment,
    created_at = EXCLUDED.created_at;

SELECT content_id, title, rating
FROM content
WHERE content_id = 1;

SELECT get_average_rating_for_content(1) AS avg_after_three_reviews;


-- =========================================================
-- 5. TEST: view vw_content_average_rating
-- Should show the same average as the function
-- =========================================================
SELECT * FROM vw_content_average_rating WHERE content_id = 1;

SELECT * FROM vw_content_average_rating
ORDER BY content_id
LIMIT 10;


-- =========================================================
-- 6. TEST: process_payment success
-- Should insert a payment and trigger receipt creation
-- =========================================================
SELECT process_payment(1, 1, 99.00, 'DKK', 'R-TEST-1001') AS new_payment_id;

SELECT * FROM payment
ORDER BY payment_id DESC
LIMIT 1;

SELECT * FROM receipt
ORDER BY receipt_id DESC
LIMIT 1;


-- =========================================================
-- 7. TEST: process_payment failure / rollback
-- Invalid subscription id should raise exception
-- Wrapped so the script continues
-- =========================================================
DO $$
BEGIN
    PERFORM process_payment(9999, 1, 99.00, 'DKK', 'R-FAIL-1');
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'Expected failure: %', SQLERRM;
END $$;

SELECT * FROM payment
ORDER BY payment_id DESC
LIMIT 5;

SELECT * FROM receipt
ORDER BY receipt_id DESC
LIMIT 5;


-- =========================================================
-- 8. TEST: cleanup_old_receipts
-- Insert an old receipt, then delete receipts older than 5 years
-- =========================================================
INSERT INTO receipt (receipt_number, price, paydate, payment_payment_id)
VALUES ('R-OLD-TEST', 10.00, NOW() - INTERVAL '6 years', 1);

SELECT cleanup_old_receipts() AS deleted_receipt_count;

SELECT * FROM receipt
WHERE receipt_number = 'R-OLD-TEST';
