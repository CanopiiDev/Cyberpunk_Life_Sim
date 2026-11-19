package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.Crime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrimeRepository extends JpaRepository<Crime, Long> {
}
