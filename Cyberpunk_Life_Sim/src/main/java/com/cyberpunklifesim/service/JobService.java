package com.cyberpunklifesim.service;

import com.cyberpunklifesim.model.Job;
import com.cyberpunklifesim.repository.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service = de laag met de logica (de "spelregels").
 * Deze service haalt jobs op uit de database.
 */
@Service
public class JobService {

    private final JobRepository jobRepository;

    // Spring geeft de repository automatisch mee (dit heet "dependency injection")
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJob(Long id) {
        // Bestaat de job niet? Dan sturen we een 404 Not Found terug
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job met id " + id + " bestaat niet"));
    }
}
