package com.cyberpunklifesim.controller;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.model.Crime;
import com.cyberpunklifesim.service.CrimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CrimeController {

    private final CrimeService crimeService;

    public CrimeController(CrimeService crimeService) {
        this.crimeService = crimeService;
    }

    // GET /api/crimes -> alle misdaden
    @GetMapping("/crimes")
    public List<Crime> getAllCrimes() {
        return crimeService.getAllCrimes();
    }

    // POST /api/characters/1/crimes/2 -> misdaad plegen
    @PostMapping("/characters/{id}/crimes/{crimeId}")
    public ActionResponse commitCrime(@PathVariable Long id, @PathVariable Long crimeId) {
        return crimeService.commitCrime(id, crimeId);
    }
}
