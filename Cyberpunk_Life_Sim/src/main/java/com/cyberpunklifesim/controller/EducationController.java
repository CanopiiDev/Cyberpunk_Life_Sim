package com.cyberpunklifesim.controller;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.model.Education;
import com.cyberpunklifesim.service.EducationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    // GET /api/education -> alle opleidingen
    @GetMapping("/education")
    public List<Education> getAllEducations() {
        return educationService.getAllEducations();
    }

    // POST /api/characters/1/education/2 -> opleiding starten
    @PostMapping("/characters/{id}/education/{educationId}")
    public ActionResponse start(@PathVariable Long id, @PathVariable Long educationId) {
        return educationService.start(id, educationId);
    }
}
