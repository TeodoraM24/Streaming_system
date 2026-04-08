package org.example.controllers;

import org.example.dtos.PlanDTO;
import org.example.entities.Plan;
import org.example.repositories.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/plans")
public class PlanController {

    @Autowired private PlanRepository planRepository;

    @GetMapping
    public List<PlanDTO> getAllActive() {
        return planRepository.findAll().stream()
                .filter(Plan::getActive)
                .map(PlanDTO::convertToDTO)
                .toList();
    }

    @PostMapping
    public PlanDTO create(@RequestBody PlanDTO dto) {
        return PlanDTO.convertToDTO(planRepository.save(new Plan(dto)));
    }
}