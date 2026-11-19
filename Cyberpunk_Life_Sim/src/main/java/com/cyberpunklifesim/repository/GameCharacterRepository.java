package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.GameCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository = de laag die met de database praat.
 * Door JpaRepository uit te breiden krijgen we gratis methodes zoals
 * findAll(), findById(), save() en deleteById(). We hoeven zelf geen SQL te schrijven.
 */
public interface GameCharacterRepository extends JpaRepository<GameCharacter, Long> {
}
