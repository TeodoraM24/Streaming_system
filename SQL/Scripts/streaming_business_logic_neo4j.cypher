// ===========================================================================
// Neo4j Browser (Cypher) equivalents of streaming_business_logic.sql
// ===========================================================================
// HOW TO USE:
//   1. Open Neo4j Browser at http://localhost:7474
//   2. Set parameters first using the :param command (see each section).
//   3. Paste and run the query for the section you need.
//
// Node labels and relationships used:
//   (:Review)-[:REVIEWS]->(:Content)
//   (:Profile)-[:WROTE]->(:Review)
//   (:Movie)-[:HAS_GENRE]->(:Genre)
//   (:Account)-[:HAS_SUBSCRIPTION]->(:Subscription)-[:HAS_PAYMENT]->(:Payment)
//   (:Payment)-[:HAS_RECEIPT]->(:Receipt)
//   (:Payment)-[:USED_PAYMENT_METHOD]->(:PaymentMethod)
// ===========================================================================


// ---------------------------------------------------------------------------
// 1. STORED FUNCTION: get_average_rating_for_content
//    PostgreSQL:  SELECT get_average_rating_for_content(1);
//    Neo4j:       Set the parameter, then run the query.
// ---------------------------------------------------------------------------

// Step 1 — set parameter:
// :param contentId => 1

// Step 2 — run query:
MATCH (r:Review)-[:REVIEWS]->(c:Content)
WHERE c.id = $contentId
RETURN round(avg(toFloat(r.rating)) * 100) / 100 AS averageRating;


// ---------------------------------------------------------------------------
// 2. VIEW: vw_content_average_rating
//    PostgreSQL:  SELECT * FROM vw_content_average_rating;
//    Neo4j:       No native views without APOC. Run the query below directly.
// ---------------------------------------------------------------------------
MATCH (c:Content)
OPTIONAL MATCH (r:Review)-[:REVIEWS]->(c)
RETURN
    c.id            AS contentId,
    c.title         AS title,
    c.originaltitle AS originalTitle,
    c.type          AS type,
    round(avg(toFloat(r.rating)) * 100) / 100 AS averageRating,
    count(r)        AS reviewCount
ORDER BY c.title;


// ---------------------------------------------------------------------------
// 3. STORED FUNCTION: find_movies_by_genre
//    PostgreSQL:  SELECT * FROM find_movies_by_genre('Drama');
//    Neo4j:       Set the parameter, then run the query.
// ---------------------------------------------------------------------------

// Step 1 — set parameter:
// :param genreName => 'Drama'

// Step 2 — run query:
MATCH (m:Movie)-[:HAS_GENRE]->(g:Genre)
WHERE toLower(g.genrename) = toLower($genreName)
RETURN
    m.id            AS contentId,
    m.title         AS title,
    m.originaltitle AS originalTitle,
    m.releasedate   AS releaseDate,
    m.duration      AS duration,
    g.genrename     AS genreName,
    m.rating        AS averageRating
ORDER BY m.title;


// ---------------------------------------------------------------------------
// 4. TRIGGER: recalculate_content_rating
//    PostgreSQL:  Fired automatically on INSERT / UPDATE / DELETE on review.
//    Neo4j:       No native triggers. Run manually after any review change.
// ---------------------------------------------------------------------------

// Step 1 — set parameter:
// :param contentId => 1

// Step 2 — run query:
MATCH (r:Review)-[:REVIEWS]->(c:Content)
WHERE c.id = $contentId
WITH c, round(avg(toFloat(r.rating)) * 100) / 100 AS newRating
SET c.rating = newRating
RETURN c.id AS contentId, c.rating AS updatedRating;


// ---------------------------------------------------------------------------
// 5. TRIGGER: create_receipt_on_paid_payment
//    PostgreSQL:  Fired automatically AFTER INSERT OR UPDATE OF status ON payment.
//    Neo4j:       No native triggers. Run manually after a payment is set to PAID.
// ---------------------------------------------------------------------------

// Step 1 — set parameter:
// :param paymentId => 101

// Step 2 — run query:
MATCH (p:Payment)
WHERE p.id = $paymentId AND p.status = 'PAID'
MERGE (p)-[:HAS_RECEIPT]->(r:Receipt {payment_id: p.id})
ON CREATE SET
    r.receiptnumber = 'R-' + toString(p.id),
    r.createdat     = p.paymentdate
ON MATCH SET
    r.createdat = p.paymentdate
RETURN p.id AS paymentId, r.receiptnumber AS receiptNumber;


// ---------------------------------------------------------------------------
// 6. STORED FUNCTION: process_payment
//    PostgreSQL:  SELECT process_payment(1, 2, 99.00, 'DKK', 'R-2000001');
//    Neo4j:       Validates subscription (ACTIVE) and payment method exist,
//                 then creates a Payment and Receipt node.
//    Returns:     The new payment node id.
// ---------------------------------------------------------------------------

// Step 1 — set parameters:
// :param subscriptionId  => 1
// :param paymentMethodId => 2
// :param price           => 99.00
// :param currency        => 'DKK'
// :param receiptNumber   => 'R-2000001'

// Step 2 — run query:
MATCH (s:Subscription)
WHERE s.id = $subscriptionId AND s.status = 'ACTIVE'
MATCH (pm:PaymentMethod)
WHERE pm.id = $paymentMethodId
WITH s, pm, timestamp() AS newId
CREATE (p:Payment {
    id:          newId,
    amount:      $price,
    currency:    $currency,
    status:      'PAID',
    paymentdate: datetime()
})
CREATE (rec:Receipt {
    id:            newId + 1,
    receiptnumber: CASE WHEN $receiptNumber IS NULL THEN 'R-' + toString(newId) ELSE $receiptNumber END,
    createdat:     datetime()
})
CREATE (p)-[:HAS_RECEIPT]->(rec)
CREATE (p)-[:USED_PAYMENT_METHOD]->(pm)
CREATE (s)-[:HAS_PAYMENT]->(p)
RETURN p.id AS newPaymentId, rec.receiptnumber AS receiptNumber;


// ---------------------------------------------------------------------------
// 7. EVENT / pg_cron: cleanup_old_receipts
//    PostgreSQL:  Scheduled via pg_cron — deletes receipts older than 5 years.
//    Neo4j:       No native scheduler. Run manually (or via external scheduler).
// ---------------------------------------------------------------------------
MATCH (r:Receipt)
WHERE r.createdat < datetime() - duration({years: 5})
WITH count(r) AS deletedCount, collect(r) AS toDelete
FOREACH (r IN toDelete | DETACH DELETE r)
RETURN deletedCount;
