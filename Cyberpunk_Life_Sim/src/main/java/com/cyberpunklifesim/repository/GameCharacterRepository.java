package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.GameCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository = de laag die met de database praat.
 * Door JpaRepository uit te breiden krijgen we gratis findAll(), findById(), save() en delete().
 * Extra methodes maakt Spring automatisch op basis van de naam, bijvoorbeeld findByCharacterId(...).
 */
public interface GameCharacterRepository extends JpaRepository<GameCharacter, Long> {
}
