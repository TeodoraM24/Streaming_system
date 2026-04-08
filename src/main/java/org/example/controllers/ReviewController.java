package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ReviewDTO;
import org.example.entities.Content;
import org.example.entities.Profile;
import org.example.entities.Review;
import org.example.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired private ReviewRepository repository;
    @Autowired private EntityManager entityManager;

    @PostMapping
    public ReviewDTO create(@RequestBody ReviewDTO dto) {
        Review review = new Review(dto);
        if (dto.getProfileId() != null) review.setProfile(entityManager.getReference(Profile.class, dto.getProfileId()));
        if (dto.getContentId() != null) review.setContent(entityManager.getReference(Content.class, dto.getContentId()));
        return ReviewDTO.convertToDTO(repository.save(review));
    }
}