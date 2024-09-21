package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;


    @GetMapping
    public List<Item> getAll(@RequestHeader(USERID_HEADER) Long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @PostMapping
    public Item create(@RequestHeader(USERID_HEADER) Long userId, @RequestBody Item item) {
        return itemService.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader(USERID_HEADER) Long userId,
                           @PathVariable Long itemId,
                           @RequestBody Item item) {
        return itemService.update(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public Boolean delete(@PathVariable Long itemId) {
        return itemService.delete(itemId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
