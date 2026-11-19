package com.cyberpunklifesim.controller;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.dto.CareerResponse;
import com.cyberpunklifesim.model.Career;
import com.cyberpunklifesim.service.CareerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CareerController {

    private final CareerService careerService;

    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    // GET /api/careers -> alle carrières met hun eisen
    @GetMapping("/careers")
    public List<Career> getAllCareers() {
        return careerService.getAllCareers();
    }

    // GET /api/characters/1/career -> huidige baan
    @GetMapping("/characters/{id}/career")
    public CareerResponse getCareer(@PathVariable Long id) {
        return careerService.getCareerStatus(id);
    }

    // POST /api/characters/1/career/apply/2 -> solliciteren
    @PostMapping("/characters/{id}/career/apply/{careerId}")
    public ActionResponse apply(@PathVariable Long id, @PathVariable Long careerId) {
        return careerService.apply(id, careerId);
    }

    // POST /api/characters/1/career/quit -> ontslag nemen
    @PostMapping("/characters/{id}/career/quit")
    public ActionResponse quit(@PathVariable Long id) {
        return careerService.quit(id);
    }
}
