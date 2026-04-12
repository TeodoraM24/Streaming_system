package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ReviewDTO;
import org.example.entities.Content;
import org.example.entities.Profile;
import org.example.entities.Review;
import org.example.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired private ReviewRepository repository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<ReviewDTO> getAll() {
        return repository.findAll().stream().map(ReviewDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public ReviewDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(ReviewDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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

    @PutMapping("/{id}")
    public ReviewDTO update(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        Review entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setTitle(dto.getTitle());
        entity.setRating(dto.getRating());
        entity.setComment(dto.getComment());
        entity.setCreatedAt(dto.getCreatedAt());
        return ReviewDTO.convertToDTO(repository.save(entity));
    }

    @PatchMapping("/{id}")
    public ReviewDTO patch(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        Review entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getRating() != null) entity.setRating(dto.getRating());
        if (dto.getComment() != null) entity.setComment(dto.getComment());
        if (dto.getCreatedAt() != null) entity.setCreatedAt(dto.getCreatedAt());
        return ReviewDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}