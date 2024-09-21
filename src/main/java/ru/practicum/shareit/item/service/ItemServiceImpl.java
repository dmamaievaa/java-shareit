package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.repository.UserStorage;
import ru.practicum.shareit.item.validator.ItemValidator;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String ITEM_NOT_FOUND = "Item not found.";
    private static final String USER_NOT_FOUND = "User not found.";
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;


    public List<Item> getAllByUserId(Long userId) {
        return itemStorage.getAllByUserId(userId);
    }

    public Item getItem(Long itemId) {
        if (!itemStorage.isItemExist(itemId)) {
            throw new NotFoundException(ITEM_NOT_FOUND);
        }
        return itemStorage.getItem(itemId);
    }

    public Item create(Long userId, Item item) {
        if (!userStorage.isUserExistById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND);
        }

        ItemValidator.validateItem(item);

        return itemStorage.create(userId, item);
    }

    public Item update(Long userId, Long itemId, Item item) {
        if (!itemStorage.isItemExist(itemId)) {
            throw new NotFoundException(ITEM_NOT_FOUND);
        }
        return itemStorage.update(userId, itemId, item);
    }

    public Boolean delete(Long itemId) {
        if (!itemStorage.isItemExist(itemId)) {
            throw new NotFoundException(ITEM_NOT_FOUND);
        }
        return itemStorage.delete(itemId);
    }

    public List<Item> search(String text) {
        return itemStorage.search(text);
    }
}
