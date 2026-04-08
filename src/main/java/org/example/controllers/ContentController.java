package org.example.controllers;

import org.example.dtos.ContentDTO;
import org.example.entities.Content;
import org.example.repositories.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ContentDTO create(@RequestBody ContentDTO dto) {
        return ContentDTO.convertToDTO(contentRepository.save(new Content(dto)));
    }
}