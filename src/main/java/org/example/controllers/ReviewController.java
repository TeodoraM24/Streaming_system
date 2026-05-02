package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ReviewDTO;
import org.example.entities.Content;
import org.example.entities.Profile;
import org.example.entities.Review;
import org.example.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired private ReviewRepository repository;
    @Autowired private EntityManager entityManager;

    // USER: reviews are public reads — any authenticated user can browse
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<ReviewDTO> getAll() {
        return repository.findAll().stream().map(ReviewDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ReviewDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(ReviewDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // USER: any authenticated user can post a review
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public ReviewDTO create(@RequestBody ReviewDTO dto) {
        Review entity = new Review(dto);
        if (dto.getProfileId() != null) {
            entity.setProfile(entityManager.getReference(Profile.class, dto.getProfileId()));
        }
        if (dto.getContentId() != null) {
            entity.setContent(entityManager.getReference(Content.class, dto.getContentId()));
        }
        return ReviewDTO.convertToDTO(repository.save(entity));
    }

    // USER + owns or ADMIN: only the author (or admin) may update a review
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @reviewOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public ReviewDTO update(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        Review entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setTitle(dto.getTitle());
        entity.setRating(dto.getRating());
        entity.setComment(dto.getComment());
        entity.setCreatedAt(dto.getCreatedAt());
        return ReviewDTO.convertToDTO(repository.save(entity));
    }

    // USER + owns or ADMIN
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @reviewOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public ReviewDTO patch(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        Review entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getRating() != null) entity.setRating(dto.getRating());
        if (dto.getComment() != null) entity.setComment(dto.getComment());
        if (dto.getCreatedAt() != null) entity.setCreatedAt(dto.getCreatedAt());
        return ReviewDTO.convertToDTO(repository.save(entity));
    }

    // USER + owns or ADMIN
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') and @reviewOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}