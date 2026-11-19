package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
