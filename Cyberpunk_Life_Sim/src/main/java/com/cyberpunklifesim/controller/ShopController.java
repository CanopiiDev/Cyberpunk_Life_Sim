package com.cyberpunklifesim.controller;

import com.cyberpunklifesim.dto.ActionResponse;
import com.cyberpunklifesim.model.InventoryItem;
import com.cyberpunklifesim.model.Item;
import com.cyberpunklifesim.service.ShopService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    // GET /api/items -> alle items in de winkel
    @GetMapping("/items")
    public List<Item> getAllItems() {
        return shopService.getAllItems();
    }

    // GET /api/characters/1/inventory -> gekochte items
    @GetMapping("/characters/{id}/inventory")
    public List<InventoryItem> getInventory(@PathVariable Long id) {
        return shopService.getInventory(id);
    }

    // POST /api/characters/1/items/3/buy -> item kopen
    @PostMapping("/characters/{id}/items/{itemId}/buy")
    public ActionResponse buy(@PathVariable Long id, @PathVariable Long itemId) {
        return shopService.buyItem(id, itemId);
    }
}
