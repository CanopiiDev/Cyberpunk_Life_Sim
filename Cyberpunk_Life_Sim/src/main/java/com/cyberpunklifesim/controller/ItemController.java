package com.cyberpunklifesim.controller;

import com.cyberpunklifesim.model.Item;
import com.cyberpunklifesim.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // GET /api/items  -> alle items in de winkel
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }
}
