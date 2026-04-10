package org.example.controllers;

import org.example.dtos.ContentDTO;
import org.example.entities.Content;
import org.example.repositories.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentRepository contentRepository;

    @GetMapping
    public List<ContentDTO> getAll() {
        return contentRepository.findAll().stream()
                .map(ContentDTO::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ContentDTO getById(@PathVariable Long id) {
        Content entity = contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Content not found"));

        return ContentDTO.convertToDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentDTO create(@RequestBody ContentDTO dto) {
        return ContentDTO.convertToDTO(contentRepository.save(new Content(dto)));
    }

    @PutMapping("/{id}")
    public ContentDTO update(@PathVariable Long id, @RequestBody ContentDTO dto) {
        Content entity = contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Content not found"));

        entity.setOriginaltitle(dto.getOriginaltitle());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setRating(dto.getRating());
        entity.setReleasedate(dto.getReleasedate());
        entity.setThumbnail(dto.getThumbnail());
        entity.setType(dto.getType());

        return ContentDTO.convertToDTO(contentRepository.save(entity));
    }

    @PatchMapping("/{id}")
    public ContentDTO patch(@PathVariable Long id, @RequestBody ContentDTO dto) {
        Content entity = contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Content not found"));

        if (dto.getOriginaltitle() != null) entity.setOriginaltitle(dto.getOriginaltitle());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getRating() != null) entity.setRating(dto.getRating());
        if (dto.getReleasedate() != null) entity.setReleasedate(dto.getReleasedate());
        if (dto.getThumbnail() != null) entity.setThumbnail(dto.getThumbnail());
        if (dto.getType() != null) entity.setType(dto.getType());

        return ContentDTO.convertToDTO(contentRepository.save(entity));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Content entity = contentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Content not found"));

        contentRepository.delete(entity);
    }
}