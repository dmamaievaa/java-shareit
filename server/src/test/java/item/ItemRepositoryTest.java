package item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(classes = ShareItServer.class)
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@example.com");
        owner = userRepository.save(owner);

        item1 = Item.builder()
                .name("Test Item 1")
                .description("Description 1")
                .available(true)
                .owner(owner)
                .build();

        item2 = Item.builder()
                .name("Test Item 2")
                .description("Description 2")
                .available(false)
                .owner(owner)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void testSearchAvailableItems() {
        List<Item> foundItems = itemRepository.search("Test");

        assertNotNull(foundItems);
        assertEquals(1, foundItems.size());
        assertEquals(item1.getId(), foundItems.getFirst().getId());
    }

    @Test
    void testSearchUnavailableItems() {
        List<Item> foundItems = itemRepository.search("Description 2");

        assertNotNull(foundItems);
        assertTrue(foundItems.isEmpty());
    }

    @Test
    void testGetAllByUserId() {
        List<Item> items = itemRepository.getAllByUserId(owner.getId());

        assertNotNull(items);
        assertEquals(2, items.size());
    }
}
