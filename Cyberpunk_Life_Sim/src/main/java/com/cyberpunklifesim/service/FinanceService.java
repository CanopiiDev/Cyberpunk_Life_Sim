package com.cyberpunklifesim.service;

import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.model.Transaction;
import com.cyberpunklifesim.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * De bank van het spel. ALLE geldwijzigingen lopen via deze service,
 * zodat iedere transactie wordt opgeslagen.
 */
@Service
public class FinanceService {

    private final TransactionRepository transactionRepository;

    public FinanceService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Geld erbij (positief bedrag) of eraf (negatief bedrag).
     * Het saldo mag hierdoor negatief worden: dan heeft het personage schulden.
     */
    public void change(GameCharacter character, int amount, String description) {
        if (amount == 0) {
            return;
        }
        character.setMoney(character.getMoney() + amount);
        if (amount > 0) {
            character.setTotalEarned(character.getTotalEarned() + amount);
        }
        transactionRepository.save(new Transaction(character.getId(), character.getAge(), amount,
                description, character.getMoney()));
    }

    /** Iets betalen. Kan alleen als je genoeg geld hebt (je kunt geen schulden maken in de winkel). */
    public void pay(GameCharacter character, int price, String description) {
        if (character.getMoney() < price) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Niet genoeg geld. Prijs: €$" + price + ", saldo: €$" + character.getMoney());
        }
        change(character, -price, description);
    }

    public List<Transaction> getTransactions(Long characterId) {
        return transactionRepository.findByCharacterIdOrderByIdAsc(characterId);
    }
}
