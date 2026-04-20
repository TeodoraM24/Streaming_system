-- EXAMPLE USAGE OF PROCEDURES IN SPRINGBOOT:
/*
@Query(value = "SELECT * FROM find_movies_by_genre(:genreName)", nativeQuery = true)
List<Object[]> findMoviesByGenre(@Param("genreName") String genreName);

@Query(value = "SELECT get_average_rating_for_content(:contentId)", nativeQuery = true)
BigDecimal getAverageRatingForContent(@Param("contentId") Long contentId);

@Query(value = "SELECT * FROM vw_content_average_rating", nativeQuery = true)
List<Object[]> findAverageRatings();

@Query(value = "SELECT process_payment(:subId, :pmId, :price, :currency, :receipt)", nativeQuery = true)
Long processPayment(...);
 */

-- BUSINESS LOGIC OBJECTS FOR STREAMING SYSTEM (POSTGRESQL)

DROP TRIGGER IF EXISTS trg_review_recalculate_rating_after_change ON review;
DROP TRIGGER IF EXISTS trg_payment_create_receipt ON payment;

DROP FUNCTION IF EXISTS recalculate_content_rating();
DROP FUNCTION IF EXISTS create_receipt_on_paid_payment();
DROP FUNCTION IF EXISTS get_average_rating_for_content(BIGINT);
DROP FUNCTION IF EXISTS cleanup_old_receipts();
DROP FUNCTION IF EXISTS process_payment(BIGINT, BIGINT, NUMERIC, CHAR(3), VARCHAR);

DROP VIEW IF EXISTS vw_content_average_rating;

DROP PROCEDURE IF EXISTS find_movies_by_genre(TEXT, REFCURSOR);
DROP FUNCTION IF EXISTS find_movies_by_genre(TEXT);
DROP PROCEDURE IF EXISTS process_payment(BIGINT, BIGINT, NUMERIC, CHAR(3), VARCHAR);


-- STORED FUNCTION
-- Average rating for one movie/show based on review rows.
CREATE OR REPLACE FUNCTION get_average_rating_for_content(p_content_id BIGINT)
RETURNS NUMERIC(3,2)
LANGUAGE plpgsql
AS $$
DECLARE
    v_average_rating NUMERIC(3,2);
BEGIN
    SELECT ROUND(AVG(r.rating)::NUMERIC, 2)
    INTO v_average_rating
    FROM review r
    WHERE r.content_content_id = p_content_id;

    RETURN v_average_rating;
END;
$$;

-- VIEW
-- Average rating for both movies and shows.
CREATE OR REPLACE VIEW vw_content_average_rating AS
SELECT
    c.content_id,
    c.title,
    c.originaltitle,
    c.type,
    get_average_rating_for_content(c.content_id) AS average_rating,
    COUNT(r.review_id) AS review_count
FROM content c
LEFT JOIN review r
    ON r.content_content_id = c.content_id
GROUP BY c.content_id, c.title, c.originaltitle, c.type;

-- STORED FUNCTION
-- Finds all movies that belong to the chosen genre.
-- If the genre contains multiple movies, every matching movie is returned.
-- Usage:
-- SELECT * FROM find_movies_by_genre('Drama');
CREATE OR REPLACE FUNCTION find_movies_by_genre(p_genre_name TEXT)
RETURNS TABLE (
    content_id BIGINT,
    title VARCHAR(200),
    originaltitle VARCHAR(200),
    releasedate DATE,
    duration SMALLINT,
    genrename VARCHAR(40),
    average_rating NUMERIC(3,2)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        c.content_id,
        c.title,
        c.originaltitle,
        c.releasedate,
        m.duration,
        g.genrename,
        get_average_rating_for_content(c.content_id) AS average_rating
    FROM genre g
    JOIN genre_has_content ghc
        ON ghc.genre_genre_id = g.genre_id
    JOIN content c
        ON c.content_id = ghc.content_content_id
    JOIN movie m
        ON m.content_content_id = c.content_id
    WHERE LOWER(g.genrename) = LOWER(p_genre_name)
    ORDER BY c.title;
END;
$$;

-- TRIGGER FUNCTION
-- When a payment becomes PAID, ensure the receipt gets the pay date.
CREATE OR REPLACE FUNCTION create_receipt_on_paid_payment()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.status = 'PAID' AND (TG_OP = 'INSERT' OR OLD.status IS DISTINCT FROM NEW.status) THEN
        IF EXISTS (
            SELECT 1
            FROM receipt r
            WHERE r.payment_payment_id = NEW.payment_id
        ) THEN
            UPDATE receipt
            SET
                price = NEW.price,
                paydate = COALESCE(NEW.created_at, NOW())
            WHERE payment_payment_id = NEW.payment_id;
        ELSE
            INSERT INTO receipt (
                receipt_number,
                price,
                paydate,
                payment_payment_id
            )
            VALUES (
                'R-' || NEW.payment_id::TEXT,
                NEW.price,
                COALESCE(NEW.created_at, NOW()),
                NEW.payment_id
            );
        END IF;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_payment_create_receipt
AFTER INSERT OR UPDATE OF status ON payment
FOR EACH ROW
EXECUTE FUNCTION create_receipt_on_paid_payment();

-- TRIGGER FUNCTION
-- Recalculate the stored content.rating whenever reviews change.
CREATE OR REPLACE FUNCTION recalculate_content_rating()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_content_id BIGINT;
BEGIN
    v_content_id := COALESCE(NEW.content_content_id, OLD.content_content_id);

    UPDATE content
    SET rating = get_average_rating_for_content(v_content_id)
    WHERE content_id = v_content_id;

    RETURN COALESCE(NEW, OLD);
END;
$$;

CREATE TRIGGER trg_review_recalculate_rating_after_change
AFTER INSERT OR UPDATE OR DELETE ON review
FOR EACH ROW
EXECUTE FUNCTION recalculate_content_rating();

-- TRANSACTION / PAYMENT FUNCTION
-- Inserts a payment and returns the new payment_id.
-- If validation fails, an exception is raised and the caller transaction rolls back.
-- Usage:
-- SELECT process_payment(1, 1, 99.00, 'DKK', 'R-2000001');
CREATE OR REPLACE FUNCTION process_payment(
    p_subscription_id BIGINT,
    p_paymentmethod_id BIGINT,
    p_price NUMERIC(10,2),
    p_currency CHAR(3),
    p_receipt_number VARCHAR(40) DEFAULT NULL
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_payment_id BIGINT;
    v_subscription_exists BOOLEAN;
    v_paymentmethod_exists BOOLEAN;
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM subscription s
        WHERE s.subscription_id = p_subscription_id
          AND s.status = 'ACTIVE'
    )
    INTO v_subscription_exists;

    IF NOT v_subscription_exists THEN
        RAISE EXCEPTION 'Payment failed: subscription % is not active', p_subscription_id;
    END IF;

    SELECT EXISTS (
        SELECT 1
        FROM paymentmethod pm
        WHERE pm.paymentmethod_id = p_paymentmethod_id
    )
    INTO v_paymentmethod_exists;

    IF NOT v_paymentmethod_exists THEN
        RAISE EXCEPTION 'Payment failed: payment method % does not exist', p_paymentmethod_id;
    END IF;

    IF p_price <= 0 THEN
        RAISE EXCEPTION 'Payment failed: price must be greater than 0';
    END IF;

    INSERT INTO payment (
        price,
        currency,
        created_at,
        status,
        subscription_subscription_id,
        paymentmethod_paymentmethod_id
    )
    VALUES (
        p_price,
        p_currency,
        NOW(),
        'PAID',
        p_subscription_id,
        p_paymentmethod_id
    )
    RETURNING payment_id INTO v_payment_id;

    UPDATE receipt
    SET receipt_number = COALESCE(p_receipt_number, 'R-' || v_payment_id::TEXT)
    WHERE payment_payment_id = v_payment_id;

    RETURN v_payment_id;
END;
$$;


-- EVENT EQUIVALENT FOR POSTGRESQL
-- PostgreSQL does not support MySQL-style CREATE EVENT.
-- Use pg_cron (or an external scheduler) to delete receipts older than 5 years.
CREATE OR REPLACE FUNCTION cleanup_old_receipts()
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_deleted_count INTEGER;
BEGIN
    DELETE FROM receipt
    WHERE paydate < NOW() - INTERVAL '5 years';

    GET DIAGNOSTICS v_deleted_count = ROW_COUNT;

    RETURN v_deleted_count;
END;
$$;
