package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ContentDTO;
import org.example.entities.Content;
import org.example.entities.Genre;
import org.example.entities.Movie;
import org.example.entities.Personnel;
import org.example.entities.Show;
import org.example.repositories.ContentRepository;
import org.example.repositories.MovieRepository;
import org.example.repositories.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired private ContentRepository contentRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private EntityManager entityManager;

    /**
     * GET /content — browse all content, any authenticated user.
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<ContentDTO> getAll() {
        return contentRepository.findAll().stream()
                .map(ContentDTO::convertToDTO)
                .toList();
    }

    /**
     * GET /content/{id} — get single content item, any authenticated user.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ContentDTO getById(@PathVariable Long id) {
        Content entity = contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ContentDTO.convertToDTO(entity);
    }

    /**
     * POST /content — create content entry, admin only.
     * Auto-creates the linked Movie or Show record based on type.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ContentDTO create(@RequestBody ContentDTO dto) {
        Content entity = new Content(dto);
        entity = contentRepository.save(entity);
        setRelations(entity, dto);
        entity = contentRepository.save(entity);

        switch (entity.getType()) {
            case MOVIE -> {
                Movie movie = new Movie();
                movie.setContent(entity);
                movie.setDuration(dto.getDuration());
                movieRepository.save(movie);
                entity.setMovie(movie);
            }
            case SHOW -> {
                Show show = new Show();
                show.setContent(entity);
                showRepository.save(show);
                entity.setShow(show);
            }
        }

        return ContentDTO.convertToDTO(entity);
    }

    /**
     * PUT /content/{id} — full update, admin only.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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

    /**
     * PATCH /content/{id} — partial update, admin only.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ContentDTO patch(@PathVariable Long id, @RequestBody ContentDTO dto) {
        Content entity = contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getOriginaltitle() != null) entity.setOriginaltitle(dto.getOriginaltitle());
        if (dto.getTitle() != null)         entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null)   entity.setDescription(dto.getDescription());
        if (dto.getRating() != null)        entity.setRating(dto.getRating());
        if (dto.getReleasedate() != null)   entity.setReleasedate(dto.getReleasedate());
        if (dto.getThumbnail() != null)     entity.setThumbnail(dto.getThumbnail());
        if (dto.getType() != null)          entity.setType(dto.getType());
        setRelationsPatch(entity, dto);
        return ContentDTO.convertToDTO(contentRepository.save(entity));
    }

    /**
     * DELETE /content/{id} — admin only.
     * Cascades to linked Movie/Show via DB constraint.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
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
                            .collect(Collectors.toCollection(ArrayList::new))
            );
        }
        if (dto.getPersonnelIds() != null) {
            entity.setPersonnel(
                    dto.getPersonnelIds().stream()
                            .map(id -> entityManager.getReference(Personnel.class, id))
                            .collect(Collectors.toCollection(ArrayList::new))
            );
        }
    }

    private void setRelationsPatch(Content entity, ContentDTO dto) {
        if (dto.getGenreIds() != null) {
            entity.setGenres(
                    dto.getGenreIds().stream()
                            .map(id -> entityManager.getReference(Genre.class, id))
                            .collect(Collectors.toCollection(ArrayList::new))
            );
        }
        if (dto.getPersonnelIds() != null) {
            entity.setPersonnel(
                    dto.getPersonnelIds().stream()
                            .map(id -> entityManager.getReference(Personnel.class, id))
                            .collect(Collectors.toCollection(ArrayList::new))
            );
        }
    }
}