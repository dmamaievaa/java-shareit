package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.item.validator.ItemValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Primary
@Repository
@RequiredArgsConstructor
public class InMemoryItemStorageImpl implements ItemStorage {

    private final UserService userService;
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<Item> getAllByUserId(Long userId) {
        List<Item> result = new ArrayList<>();
        for (Item i : items.values()) {
            if (i.getOwner().getId().equals(userId)) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public Item getItem(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item create(Long userId, Item item) {
        item.setId(++id);
        item.setOwner(userService.getUser(userId));
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        Item currentItem = getItem(itemId);

        if (!Objects.equals(currentItem.getOwner().getId(), userId)) {
            throw new ForbiddenException("User don't have access to this item.");
        }
        Item updatedItem = ItemValidator.itemPatch(currentItem, item);

        ItemValidator.validateItem(updatedItem);

        return updatedItem;
    }

    @Override
    public Boolean delete(Long itemId) {
        items.remove(itemId);
        return true;
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            List<Item> result = new ArrayList<>();
            String lowerCaseText = text.toLowerCase();

            for (Item i : items.values()) {
                if ((i.getName().toLowerCase().contains(lowerCaseText)
                        || i.getDescription().toLowerCase().contains(lowerCaseText))
                        && i.getAvailable()) {
                    result.add(i);
                }
            }
            return result;
        }
    }

    @Override
    public Boolean isItemExist(Long id) {
        return items.containsKey(id);
    }
}
