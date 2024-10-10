package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAll(Long userId);

    ItemDto get(Long itemId);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    Boolean delete(Long itemId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);

    List<CommentDto> getComments(Long itemId);
}

