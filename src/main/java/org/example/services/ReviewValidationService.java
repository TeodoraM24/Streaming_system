package org.example.services;

import org.example.dtos.ReviewDTO;
import org.springframework.stereotype.Service;

@Service
public class ReviewValidationService {

    private static final int MIN_TITLE_LENGTH = 1;
    private static final int MAX_TITLE_LENGTH = 60;
    private static final int MIN_COMMENT_LENGTH = 1;
    private static final int MAX_COMMENT_LENGTH = 500;
    private static final short MIN_RATING = 1;
    private static final short MAX_RATING = 10;

    public void validateCreateReview(ReviewDTO review) {
        if (review == null) {
            throw new IllegalArgumentException("Review must be provided");
        }

        validateTitle(review.getTitle());
        validateComment(review.getComment());
        validateRating(review.getRating());
    }

    public void validateTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Review title must be provided");
        }

        if (title.length() < MIN_TITLE_LENGTH || title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Review title must be between 1 and 60 characters");
        }
    }

    public void validateComment(String comment) {
        if (comment == null || comment.isEmpty()) {
            throw new IllegalArgumentException("Review comment must be provided");
        }

        if (comment.length() < MIN_COMMENT_LENGTH || comment.length() > MAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException("Review comment must be between 1 and 500 characters");
        }
    }

    public void validateRating(Short rating) {
        if (rating == null) {
            throw new IllegalArgumentException("Review rating must be provided");
        }

        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new IllegalArgumentException("Review rating must be between 1 and 10");
        }
    }
}
