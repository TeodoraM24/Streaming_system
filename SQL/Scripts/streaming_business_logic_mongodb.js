// ===========================================================================
// MongoDB Compass (mongosh) equivalents of streaming_business_logic.sql
// ===========================================================================
// HOW TO USE:
//   1. Open MongoDB Compass
//   2. Click the ">_MONGOSH" tab at the bottom
//   3. Switch to your database:  use streaming_system
//      (replace "streaming_system" with your actual database name)
//   4. Paste and run each section below as needed.
//
// Collections used:
//   - accounts  : AccountDocument  (contains profiles -> reviews, subscriptions -> payments -> receipt)
//   - content   : ContentDocument  (contains rating, genres, movie)
// ===========================================================================

// ---------------------------------------------------------------------------
// 1. STORED FUNCTION: get_average_rating_for_content
//    PostgreSQL:  SELECT get_average_rating_for_content(1);
//    MongoDB:     getAverageRatingForContent(1)
// ---------------------------------------------------------------------------
function getAverageRatingForContent(contentId) {
    var result = db.accounts.aggregate([
        { $unwind: { path: "$profiles", preserveNullAndEmptyArrays: false } },
        { $unwind: { path: "$profiles.reviews", preserveNullAndEmptyArrays: false } },
        { $match: { "profiles.reviews.contentId": { $in: [contentId, String(contentId), parseInt(contentId), Long(contentId)] } } },
        {
            $group: {
                _id: null,
                averageRating: { $avg: "$profiles.reviews.rating" }
            }
        },
        {
            $project: {
                _id: 0,
                averageRating: { $round: ["$averageRating", 2] }
            }
        }
    ]).toArray();

    return result.length > 0 ? result[0].averageRating : null;
}

// Test:
getAverageRatingForContent(71);


// ---------------------------------------------------------------------------
// 2. VIEW: vw_content_average_rating
//    PostgreSQL:  SELECT * FROM vw_content_average_rating;
//    MongoDB:     db.vw_content_average_rating.find().pretty()
//
//    Run the createView block ONCE to create the view.
//    After that, just query it like a normal collection.
// ---------------------------------------------------------------------------
db.createView("vw_content_average_rating", "content", [
    {
        $lookup: {
            from: "accounts",
            let: { cid: "$contentId" },
            pipeline: [
                { $unwind: { path: "$profiles", preserveNullAndEmptyArrays: false } },
                { $unwind: { path: "$profiles.reviews", preserveNullAndEmptyArrays: false } },
                { $match: { $expr: { $eq: ["$profiles.reviews.contentId", "$$cid"] } } },
                {
                    $group: {
                        _id: null,
                        averageRating: { $avg: "$profiles.reviews.rating" },
                        reviewCount: { $sum: 1 }
                    }
                }
            ],
            as: "reviewStats"
        }
    },
    {
        $addFields: {
            averageRating: {
                $round: [
                    { $ifNull: [{ $arrayElemAt: ["$reviewStats.averageRating", 0] }, null] },
                    2
                ]
            },
            reviewCount: { $ifNull: [{ $arrayElemAt: ["$reviewStats.reviewCount", 0] }, 0] }
        }
    },
    {
        $project: {
            _id: 0,
            contentId: 1,
            title: 1,
            originalTitle: 1,
            type: 1,
            averageRating: 1,
            reviewCount: 1
        }
    }
]);

// Query the view:
db.vw_content_average_rating.find().pretty();


// ---------------------------------------------------------------------------
// 3. STORED FUNCTION: find_movies_by_genre
//    PostgreSQL:  SELECT * FROM find_movies_by_genre('Drama');
//    MongoDB:     findMoviesByGenre("Drama")
// ---------------------------------------------------------------------------
function findMoviesByGenre(genreName) {
    return db.content.aggregate([
        {
            $match: {
                "movie": { $ne: null },
                "genres.genrename": { $regex: new RegExp("^" + genreName + "$", "i") }
            }
        },
        {
            $addFields: {
                matchedGenre: {
                    $filter: {
                        input: "$genres",
                        as: "g",
                        cond: {
                            $regexMatch: {
                                input: "$$g.genrename",
                                regex: new RegExp("^" + genreName + "$", "i")
                            }
                        }
                    }
                }
            }
        },
        {
            $project: {
                _id: 0,
                contentId: 1,
                title: 1,
                originalTitle: 1,
                releaseDate: 1,
                duration: "$movie.duration",
                genreName: { $arrayElemAt: ["$matchedGenre.genrename", 0] },
                averageRating: "$rating"
            }
        },
        { $sort: { title: 1 } }
    ]).toArray();
}

// Test:
findMoviesByGenre("Drama");


// ---------------------------------------------------------------------------
// 4. TRIGGER: recalculate_content_rating
//    PostgreSQL:  Fired automatically on INSERT / UPDATE / DELETE on review.
//    MongoDB:     Call manually after any review change.
//                 recalculateContentRating(1)
// ---------------------------------------------------------------------------
function recalculateContentRating(contentId) {
    var avg = getAverageRatingForContent(contentId);
    db.content.updateOne(
        { contentId: contentId },
        { $set: { rating: avg } }
    );
    print("content " + contentId + " rating updated to: " + avg);
}

// Test:
recalculateContentRating(3);


// ---------------------------------------------------------------------------
// 5. TRIGGER: create_receipt_on_paid_payment
//    PostgreSQL:  Fired automatically AFTER INSERT OR UPDATE OF status ON payment.
//    MongoDB:     Call manually after setting a payment status to PAID.
//                 syncReceiptOnPaidPayment(subscriptionId, paymentId, price, new Date())
// ---------------------------------------------------------------------------
function syncReceiptOnPaidPayment(subscriptionId, paymentId, price, payDate) {
    db.accounts.find({
        "subscriptions.subscriptionId": subscriptionId
    }).forEach(function (account) {
        var modified = false;

        account.subscriptions.forEach(function (sub) {
            if (sub.subscriptionId !== subscriptionId) return;
            if (!sub.payments) return;

            sub.payments.forEach(function (payment) {
                if (payment.paymentId !== paymentId) return;
                if (payment.status !== "PAID") return;

                if (payment.receipt) {
                    payment.receipt.price = price;
                    payment.receipt.paydate = payDate;
                } else {
                    payment.receipt = {
                        receiptNumber: "R-" + paymentId,
                        price: price,
                        paydate: payDate,
                        paymentId: paymentId
                    };
                }
                modified = true;
            });
        });

        if (modified) {
            db.accounts.replaceOne({ _id: account._id }, account);
            print("Receipt synced for paymentId " + paymentId);
        }
    });
}

// Test:
syncReceiptOnPaidPayment(1, 101, 99.00, new Date());


// ---------------------------------------------------------------------------
// 6. STORED FUNCTION: process_payment
//    PostgreSQL:  SELECT process_payment(1, 2, 99.00, 'DKK', 'R-2000001');
//    MongoDB:     processPayment(1, 2, 99.00, "DKK", "R-2000001")
//    Validates subscription (must be ACTIVE), payment method, and price,
//    then inserts a new payment with embedded receipt.
//    Returns the generated paymentId.
// ---------------------------------------------------------------------------
function processPayment(subscriptionId, paymentMethodId, price, currency, receiptNumber) {
    if (!price || price <= 0) {
        throw new Error("Payment failed: price must be greater than 0");
    }

    var targetAccount = null;
    var targetSub = null;

    db.accounts.find({ "subscriptions.subscriptionId": subscriptionId }).forEach(function (acc) {
        acc.subscriptions.forEach(function (sub) {
            if (sub.subscriptionId === subscriptionId) {
                if (sub.status !== "ACTIVE") {
                    throw new Error("Payment failed: subscription " + subscriptionId + " is not active");
                }
                targetAccount = acc;
                targetSub = sub;
            }
        });
    });

    if (!targetAccount) {
        throw new Error("Payment failed: subscription " + subscriptionId + " not found");
    }

    var paymentMethod = null;
    if (targetAccount.paymentMethods) {
        targetAccount.paymentMethods.forEach(function (pm) {
            if (pm.paymentmethodId === paymentMethodId) paymentMethod = pm;
        });
    }

    if (!paymentMethod) {
        throw new Error("Payment failed: payment method " + paymentMethodId + " does not exist");
    }

    var newPaymentId = new Date().getTime();
    var now = new Date();
    var resolvedReceiptNumber = (receiptNumber && receiptNumber.trim() !== "")
        ? receiptNumber
        : "R-" + newPaymentId;

    var newPayment = {
        paymentId: newPaymentId,
        price: price,
        currency: currency,
        createdAt: now,
        status: "PAID",
        subscriptionId: subscriptionId,
        paymentMethodId: paymentMethodId,
        paymentMethod: paymentMethod,
        receipt: {
            receiptNumber: resolvedReceiptNumber,
            price: price,
            paydate: now,
            paymentId: newPaymentId
        }
    };

    db.accounts.updateOne(
        {
            _id: targetAccount._id,
            "subscriptions.subscriptionId": subscriptionId
        },
        { $push: { "subscriptions.$.payments": newPayment } }
    );

    print("Payment processed. New paymentId: " + newPaymentId);
    return newPaymentId;
}

// Test:
processPayment(1, 2, 99.00, "DKK", null);


// ---------------------------------------------------------------------------
// 7. EVENT / pg_cron: cleanup_old_receipts
//    PostgreSQL:  Scheduled via pg_cron — deletes receipts older than 5 years.
//    MongoDB:     cleanupOldReceipts()
// ---------------------------------------------------------------------------
function cleanupOldReceipts() {
    var cutoff = new Date();
    cutoff.setFullYear(cutoff.getFullYear() - 5);
    var deletedCount = 0;

    db.accounts.find({
        "subscriptions.payments.receipt.paydate": { $lt: cutoff }
    }).forEach(function (account) {
        var modified = false;

        account.subscriptions.forEach(function (sub) {
            if (!sub.payments) return;
            sub.payments.forEach(function (payment) {
                if (payment.receipt &&
                    payment.receipt.paydate &&
                    payment.receipt.paydate < cutoff) {
                    payment.receipt = null;
                    deletedCount++;
                    modified = true;
                }
            });
        });

        if (modified) {
            db.accounts.replaceOne({ _id: account._id }, account);
        }
    });

    print("Cleaned up " + deletedCount + " old receipt(s).");
    return deletedCount;
}

// Test:
cleanupOldReceipts();
