package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getItems() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        if (itemRepository.findById(id).isPresent()) {
            log.info("ItemInfo: " + itemRepository.findById(id).get().getName() +
                    "  " + itemRepository.findById(id).get().getPrice() + "  " +
                    itemRepository.findById(id).get().getDescription());
            return ResponseEntity.of(itemRepository.findById(id));
        }
        log.error("Item not found");
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
        List<Item> items = itemRepository.findByName(name);
        if (items == null || items.isEmpty()) {
            log.error("item not found");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(items);
    }

}
