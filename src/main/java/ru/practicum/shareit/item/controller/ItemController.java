package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.utils.GlobalConstants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId) {
        log.info("Fetching all items for user ID: {}", userId);
        List<ItemDto> items = itemService.findAll(userId);
        log.info("Fetched {} items for user ID: {}", items.size(), userId);
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        log.info("Fetching item details for item ID: {}", itemId);
        ItemDto item = itemService.get(itemId);
        log.info("Fetched item details: {}", item);
        return item;
    }

    @PostMapping
    public ItemDto create(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item for user ID: {} with item details: {}", userId, itemDto);
        ItemDto createdItem = itemService.create(userId, itemDto);
        log.info("Item successfully created with ID: {}", createdItem.getId());
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Updating item ID: {} for user ID: {} with new details: {}", itemId, userId, itemDto);
        ItemDto updatedItem = itemService.update(userId, itemId, itemDto);
        log.info("Item ID: {} successfully updated with new details: {}", itemId, updatedItem);
        return updatedItem;
    }

    @DeleteMapping("/{itemId}")
    public Boolean delete(@PathVariable Long itemId) {
        log.info("Deleting item with ID: {}", itemId);
        Boolean isDeleted = itemService.delete(itemId);
        log.info("Item with ID: {} deletion status: {}", itemId, isDeleted ? "success" : "failure");
        return isDeleted;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Searching items with text: '{}'", text);

        if (text == null || text.trim().isEmpty()) {
            log.info("Search text is empty, returning an empty list.");
            return List.of();
        }

        List<ItemDto> foundItems = itemService.search(text);
        log.info("Found {} items matching the search criteria", foundItems.size());
        return foundItems;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody @Valid CommentDto commentDto) {
        log.info("Adding comment for item ID: {} by user ID: {} with comment details: {}", itemId, userId, commentDto);
        CommentDto createdComment = itemService.addComment(itemId, userId, commentDto);
        log.info("Comment successfully added with ID: {} for item ID: {}", createdComment.getId(), itemId);
        return createdComment;
    }
}
