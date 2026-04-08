package org.example.controllers;

import org.example.dtos.PersonnelDTO;
import org.example.entities.Personnel;
import org.example.repositories.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/personnel")
public class PersonnelController {

    @Autowired private PersonnelRepository personnelRepository;

    @GetMapping
    public List<PersonnelDTO> getAll() {
        return personnelRepository.findAll().stream().map(PersonnelDTO::convertToDTO).toList();
    }

    @PostMapping
    public PersonnelDTO create(@RequestBody PersonnelDTO dto) {
        return PersonnelDTO.convertToDTO(personnelRepository.save(new Personnel(dto)));
    }
}