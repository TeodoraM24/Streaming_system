package org.example.controllers;

import org.example.dtos.ListsDTO;
import org.example.entities.Lists;
import org.example.repositories.ListsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lists")
public class ListsController {

    @Autowired
    private ListsRepository listsRepository;

    @PostMapping
    public ListsDTO create(@RequestBody ListsDTO dto) {
        Lists list = new Lists(dto);
        return ListsDTO.convertToDTO(listsRepository.save(list));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        listsRepository.deleteById(id);
    }
}