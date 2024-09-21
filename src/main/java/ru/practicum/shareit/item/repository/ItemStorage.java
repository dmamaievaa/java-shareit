package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> getAllByUserId(Long userId);

    Item getItem(Long itemId);

    Item create(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);

    Boolean delete(Long itemId);

    Boolean isItemExist(Long id);

    List<Item> search(String text);
}
