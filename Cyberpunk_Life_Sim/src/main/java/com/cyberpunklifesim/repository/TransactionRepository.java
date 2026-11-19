package com.cyberpunklifesim.repository;

import com.cyberpunklifesim.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCharacterIdOrderByIdAsc(Long characterId);

    void deleteByCharacterId(Long characterId);
}
