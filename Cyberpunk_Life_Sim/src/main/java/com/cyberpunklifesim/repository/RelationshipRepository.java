package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    List<Relationship> findByCharacterId(Long characterId);

    List<Relationship> findByCharacterIdAndType(Long characterId, String type);

    void deleteByCharacterId(Long characterId);
}
