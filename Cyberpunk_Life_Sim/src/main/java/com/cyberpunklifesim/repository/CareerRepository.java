package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.Career;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository extends JpaRepository<Career, Long> {

    Career findByName(String name);
}
