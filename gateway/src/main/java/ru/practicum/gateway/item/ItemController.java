package ru.practicum.gateway.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Started creating new item");
        final ResponseEntity<Object> item = itemClient.create(ownerId, itemDto);
        log.info("Finished creating new item");
        return item;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto,
                                         @PathVariable(value = "itemId") Long itemId,
                                         @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Started updating item with id {}", itemId);
        final ResponseEntity<Object> item = itemClient.update(itemId, ownerId, itemDto);
        log.info("Finished updating item with id {}", itemId);
        return item;
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Started getting all items");
        final ResponseEntity<Object> item = itemClient.findAll(ownerId);
        log.info("Finished getting all items");
        return item;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable(value = "itemId") Long itemId,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started getting item by id = {}", itemId);
        final ResponseEntity<Object> item = itemClient.getItemById(itemId, userId);
        log.info("Finished getting item by id = {}", itemId);
        return item;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text") String text,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started searching item contained text: {}", text);
        final ResponseEntity<Object> item = itemClient.search(userId, text);
        log.info("Finished searching item contained text: {}", text);
        return item;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentDto,
                                                @PathVariable(name = "itemId") Long itemId,
                                                @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Started creating comment with itemId = {}", itemId);
        final ResponseEntity<Object> comment = itemClient.addComment(itemId, userId, commentDto);
        log.info("Generated creating comment with itemId = {}", itemId);
        return comment;
    }
}