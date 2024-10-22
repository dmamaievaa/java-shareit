package item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.item.service.ItemServiceImpl;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {

    private final ItemServiceImpl itemService;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test.user@example.com");
        user = userRepository.save(user);
    }

    @Test
    void testFindAllItemsByUserId() {
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Item description 1");
        item1.setOwner(user);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Item description 2");
        item2.setOwner(user);
        itemRepository.save(item2);

        List<ItemDto> items = itemService.findAll(user.getId());

        assertThat(items).hasSize(2);
        assertThat(items).extracting(ItemDto::getName).containsExactlyInAnyOrder("Item 1", "Item 2");
    }
}