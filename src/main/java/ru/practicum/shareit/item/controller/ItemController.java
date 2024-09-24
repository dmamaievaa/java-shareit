package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.utils.GlobalConstant;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(GlobalConstant.USERID_HEADER) Long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(GlobalConstant.USERID_HEADER) Long userId, @RequestBody @Valid ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(GlobalConstant.USERID_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public Boolean delete(@PathVariable Long itemId) {
        return itemService.delete(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
