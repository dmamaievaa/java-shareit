package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

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
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public Item getItem(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item create(Long userId, Item item) {
        item.setId(++id);
        UserDto userDto = userService.getUser(userId);
        User owner = UserMapper.toUser(userDto);
        item.setOwner(owner);
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        Item currentItem = getItem(itemId);

        if (!Objects.equals(currentItem.getOwner().getId(), userId)) {
            throw new ForbiddenException("User don't have access to this item.");
        }

        Item updatedItem = ItemMapper.itemPatch(currentItem, item);

        items.put(itemId, updatedItem);
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
        }

        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(lowerCaseText) ||
                        item.getDescription().toLowerCase().contains(lowerCaseText)) &&
                        item.getAvailable())
                .toList();
    }

    @Override
    public Boolean isItemExist(Long id) {
        return items.containsKey(id);
    }
}
