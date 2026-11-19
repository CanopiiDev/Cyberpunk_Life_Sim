package com.cyberpunklifesim.controller;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.model.Relationship;
import com.cyberpunklifesim.service.RelationshipService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/characters/{id}/relationships")
public class RelationshipController {

    private final RelationshipService relationshipService;

    public RelationshipController(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    // GET /api/characters/1/relationships -> vrienden, partner, familie en vijanden
    @GetMapping
    public List<Relationship> getRelationships(@PathVariable Long id) {
        return relationshipService.getRelationships(id);
    }

    // POST /api/characters/1/relationships/4/spend-time -> tijd doorbrengen (relatie wordt beter)
    @PostMapping("/{relationshipId}/spend-time")
    public ActionResponse spendTime(@PathVariable Long id, @PathVariable Long relationshipId) {
        return relationshipService.spendTime(id, relationshipId);
    }
}
