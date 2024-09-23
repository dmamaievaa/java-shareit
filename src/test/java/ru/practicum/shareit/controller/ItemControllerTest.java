package ru.practicum.shareit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.repository.InMemoryItemStorageImpl;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserStorageImpl;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemControllerTest {

    private ItemController itemController;
    private InMemoryItemStorageImpl itemStorage;
    private InMemoryUserStorageImpl userStorage;
    private UserService userService;
    private ItemService itemService;
    private User user1;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorageImpl();
        userService = new UserServiceImpl(userStorage);
        itemStorage = new InMemoryItemStorageImpl(userService);
        itemService = new ItemServiceImpl(itemStorage, userStorage);
        itemController = new ItemController(itemService);

        user1 = User.builder()
                .id(1L)
                .name("User 1")
                .email("user1@example.com")
                .build();

        userService.create(user1);
    }


    @Test
    void shouldCreateItem() {
        Item item = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        Item createdItem = itemController.create(user1.getId(), item);

        assertNotNull(createdItem);
        assertEquals(1L, createdItem.getId());
        assertEquals("Item 1", createdItem.getName());
        assertEquals("Description 1", createdItem.getDescription());
    }

    @Test
    void shouldUpdateItem() {
        Item item = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        Item createdItem = itemController.create(user1.getId(), item);

        Item updatedItem = Item.builder()
                .name("Updated Item 1")
                .description("Updated Description")
                .available(false)
                .build();

        Item result = itemController.update(user1.getId(), createdItem.getId(), updatedItem);

        assertNotNull(result);
        assertEquals(createdItem.getId(), result.getId());
        assertEquals("Updated Item 1", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void shouldGetItemById() {
        Item item = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        Item createdItem = itemController.create(user1.getId(), item);

        Item foundItem = itemController.getItem(createdItem.getId());

        assertNotNull(foundItem);
        assertEquals(createdItem.getId(), foundItem.getId());
    }

    @Test
    void shouldDeleteItem() {
        Item item = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        Item createdItem = itemController.create(user1.getId(), item);

        Boolean deleteResult = itemController.delete(createdItem.getId());
        assertTrue(deleteResult);

        assertThrows(NotFoundException.class, () -> itemController.getItem(createdItem.getId()));
    }

    @Test
    void shouldSearchItems() {
        Item item1 = Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();
        itemController.create(user1.getId(), item1);

        Item item2 = Item.builder()
                .name("Hammer")
                .description("Heavy hammer")
                .available(true)
                .build();
        itemController.create(user1.getId(), item2);

        List<Item> foundItems = itemController.search("drill");
        assertEquals(1, foundItems.size());
        assertEquals("Drill", foundItems.getFirst().getName());
    }

    @Test
    void shouldGetAllItemsForUser() {
        Item item1 = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        itemController.create(user1.getId(), item1);

        Item item2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .build();
        itemController.create(user1.getId(), item2);

        List<Item> items = itemController.getAll(user1.getId());

        assertEquals(2, items.size());
    }
}