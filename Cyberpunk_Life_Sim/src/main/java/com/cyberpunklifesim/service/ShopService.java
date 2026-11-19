package com.cyberpunklifesim.service;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.model.GameCharacter;
import com.cyberpunklifesim.model.InventoryItem;
import com.cyberpunklifesim.model.Item;
import com.cyberpunklifesim.repository.InventoryItemRepository;
import com.cyberpunklifesim.repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * De winkel en de inventory. Cyberware is hier ook te koop (inclusief installatie).
 */
@Service
public class ShopService {

    public static final String CYBERWARE = "Cyberware";

    private final ItemRepository itemRepository;
    private final InventoryItemRepository inventoryRepository;
    private final CharacterService characterService;
    private final FinanceService financeService;
    private final LifeHistoryService historyService;

    public ShopService(ItemRepository itemRepository, InventoryItemRepository inventoryRepository,
                       CharacterService characterService, FinanceService financeService,
                       LifeHistoryService historyService) {
        this.itemRepository = itemRepository;
        this.inventoryRepository = inventoryRepository;
        this.characterService = characterService;
        this.financeService = financeService;
        this.historyService = historyService;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<InventoryItem> getInventory(Long characterId) {
        characterService.getCharacter(characterId); // 404 als het personage niet bestaat
        return inventoryRepository.findByCharacterId(characterId);
    }

    public boolean hasCyberware(Long characterId) {
        return inventoryRepository.existsByCharacterIdAndItem_Category(characterId, CYBERWARE);
    }

    /** Opgeteld: hoeveel procent minder kans je hebt om gepakt te worden (bijvoorbeeld door een Reflex Booster). */
    public int getCrimeBonus(Long characterId) {
        return inventoryRepository.findByCharacterId(characterId).stream()
                .mapToInt(inventoryItem -> inventoryItem.getItem().getCrimeBonus())
                .sum();
    }

    public ActionResponse buyItem(Long characterId, Long itemId) {
        GameCharacter character = characterService.getFreeCharacter(characterId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item met id " + itemId + " bestaat niet"));

        boolean isCyberware = item.getCategory().equals(CYBERWARE);
        if (isCyberware && inventoryRepository.existsByCharacterIdAndItemId(characterId, itemId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Je hebt " + item.getName() + " al laten installeren.");
        }

        // Betalen (gooit een fout als je niet genoeg geld hebt)
        financeService.pay(character, item.getPrice(), "Gekocht: " + item.getName());
        inventoryRepository.save(new InventoryItem(characterId, item, character.getAge()));

        // Bonussen (en bijwerkingen) toepassen
        character.changeHealth(item.getHealthBonus());
        character.changeHappiness(item.getHappinessBonus());
        character.changeIntelligence(item.getIntelligenceBonus());
        character.changeAppearance(item.getAppearanceBonus());

        String message;
        if (isCyberware) {
            message = "Je liet " + item.getName() + " installeren voor €$" + item.getPrice() + ".";
            historyService.logImportant(character, message);
        } else {
            message = "Je kocht " + item.getName() + " voor €$" + item.getPrice() + ".";
            historyService.log(character, message);
        }
        if (item.getSideEffect() != null) {
            message += " Bijwerking: " + item.getSideEffect();
        }

        characterService.save(character);
        return new ActionResponse(true, message, characterService.toResponse(character));
    }
}
