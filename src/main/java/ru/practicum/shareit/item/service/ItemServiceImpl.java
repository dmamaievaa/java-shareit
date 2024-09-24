package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String ITEM_NOT_FOUND = "Item not found.";
    private static final String USER_NOT_FOUND = "User not found.";
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public List<ItemDto> getAllByUserId(Long userId) {
        List<Item> items = itemStorage.getAllByUserId(userId);
        return ItemMapper.toItemDtoList(items);
    }

    public ItemDto getItem(Long itemId) {
        if (!itemStorage.isItemExist(itemId)) {
            throw new NotFoundException(ITEM_NOT_FOUND);
        }
        Item item = itemStorage.getItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto create(Long userId, ItemDto itemDto) {
        if (!userStorage.isUserExistById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND);
        }

        Item item = ItemMapper.toItem(itemDto);
        Item createdItem = itemStorage.create(userId, item);
        return ItemMapper.toItemDto(createdItem);
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (!itemStorage.isItemExist(itemId)) {
            throw new NotFoundException(ITEM_NOT_FOUND);
        }

        Item item = ItemMapper.toItem(itemDto);
        Item updatedItem = itemStorage.update(userId, itemId, item);
        return ItemMapper.toItemDto(updatedItem);
    }

    public Boolean delete(Long itemId) {
        if (!itemStorage.isItemExist(itemId)) {
            throw new NotFoundException(ITEM_NOT_FOUND);
        }
        return itemStorage.delete(itemId);
    }

    public List<ItemDto> search(String text) {
        List<Item> items = itemStorage.search(text);
        return ItemMapper.toItemDtoList(items);
    }
}
