package item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.Instant;
import java.util.Collections;

public class ItemTest {

    @Test
    void shouldBeEqual() {
        User owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        Instant now = Instant.now();

        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(owner)
                .lastBooking(now)
                .nextBooking(now)
                .comments(Collections.emptyList())
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(owner)
                .lastBooking(now)
                .nextBooking(now)
                .comments(Collections.emptyList())
                .build();

        Assertions.assertEquals(item1, item2, "Items with the same id should be equal");
    }

    @Test
    void shouldNotBeEqual() {
        User owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(owner)
                .lastBooking(Instant.now())
                .nextBooking(Instant.now())
                .comments(Collections.emptyList())
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .owner(owner)
                .lastBooking(Instant.now())
                .nextBooking(Instant.now())
                .comments(Collections.emptyList())
                .build();

        Assertions.assertNotEquals(item1, item2, "Items with different ids should not be equal");
    }

    @Test
    @DisplayName("Should return correct hashcode based on id")
    void shouldReturnCorrectHashcode() {
        User owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(owner)
                .lastBooking(Instant.now())
                .nextBooking(Instant.now())
                .comments(Collections.emptyList())
                .build();

        int expectedHashcode = item1.getId().hashCode();

        Assertions.assertEquals(expectedHashcode, item1.hashCode(),
                "Hashcode should be based on the item's id");
    }
}

