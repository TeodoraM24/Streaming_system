package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ContentDTO;
import org.example.entities.Content;
import org.example.entities.Genre;
import org.example.entities.Personnel;
import org.example.repositories.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private EntityManager entityManager;

    @GetMapping
    public List<ContentDTO> getAll() {
        return contentRepository.findAll()
                .stream()
                .map(ContentDTO::convertToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ContentDTO getById(@PathVariable Long id) {
        Content entity = contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ContentDTO.convertToDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentDTO create(@RequestBody ContentDTO dto) {
        Content entity = new Content(dto);
        entity = contentRepository.save(entity);
        setRelations(entity, dto);
        return ContentDTO.convertToDTO(contentRepository.save(entity));
    }

    @PutMapping("/{id}")
    public ContentDTO update(@PathVariable Long id, @RequestBody ContentDTO dto) {
        Content entity = contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setOriginaltitle(dto.getOriginaltitle());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setRating(dto.getRating());
        entity.setReleasedate(dto.getReleasedate());
        entity.setThumbnail(dto.getThumbnail());
        entity.setType(dto.getType());
        setRelations(entity, dto);
        return ContentDTO.convertToDTO(contentRepository.save(entity));
    }

    @PatchMapping("/{id}")
    public ContentDTO patch(@PathVariable Long id, @RequestBody ContentDTO dto) {
        Content entity = contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getOriginaltitle() != null) entity.setOriginaltitle(dto.getOriginaltitle());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getRating() != null) entity.setRating(dto.getRating());
        if (dto.getReleasedate() != null) entity.setReleasedate(dto.getReleasedate());
        if (dto.getThumbnail() != null) entity.setThumbnail(dto.getThumbnail());
        if (dto.getType() != null) entity.setType(dto.getType());
        setRelationsPatch(entity, dto);
        return ContentDTO.convertToDTO(contentRepository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Content entity = contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        contentRepository.delete(entity);
    }

    private void setRelations(Content entity, ContentDTO dto) {
        if (dto.getGenreIds() != null) {
            entity.setGenres(
                    dto.getGenreIds().stream()
                            .map(id -> entityManager.getReference(Genre.class, id))
                            .collect(java.util.stream.Collectors.toCollection(ArrayList::new))
            );
        }
        if (dto.getPersonnelIds() != null) {
            entity.setPersonnel(
                    dto.getPersonnelIds().stream()
                            .map(id -> entityManager.getReference(Personnel.class, id))
                            .collect(java.util.stream.Collectors.toCollection(ArrayList::new))
            );
        }
    }

    private void setRelationsPatch(Content entity, ContentDTO dto) {
        if (dto.getGenreIds() != null) {
            entity.setGenres(
                    dto.getGenreIds().stream()
                            .map(id -> entityManager.getReference(Genre.class, id))
                            .collect(java.util.stream.Collectors.toCollection(ArrayList::new))
            );
        }
        if (dto.getPersonnelIds() != null) {
            entity.setPersonnel(
                    dto.getPersonnelIds().stream()
                            .map(id -> entityManager.getReference(Personnel.class, id))
                            .collect(java.util.stream.Collectors.toCollection(ArrayList::new))
            );
        }
    }
}