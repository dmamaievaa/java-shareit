package item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.enums.Status;
import ru.practicum.server.exception.InvalidParamException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.item.service.ItemServiceImpl;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@Rollback
public class ItemServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

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

    @Test
    void testCreateItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);

        ItemDto createdItem = itemService.create(user.getId(), itemDto);

        assertThat(createdItem.getName()).isEqualTo("New Item");
        assertThat(createdItem.getDescription()).isEqualTo("Item description");
        assertThat(createdItem.getAvailable()).isTrue();
    }

    @Test
    void testCreateItemWithNonExistentUserThrowsException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("Item description");

        assertThrows(NotFoundException.class, () -> itemService.create(999L, itemDto));
    }

    @Test
    void testUpdateItem() {
        Item item = new Item();
        item.setName("Old Item");
        item.setDescription("Old description");
        item.setOwner(user);
        item = itemRepository.save(item);

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Updated description");

        ItemDto updatedItem = itemService.update(user.getId(), item.getId(), updatedItemDto);

        assertThat(updatedItem.getName()).isEqualTo("Updated Item");
        assertThat(updatedItem.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void testUpdateItemWithNonExistentItemThrowsException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");

        assertThrows(NotFoundException.class, () -> itemService.update(user.getId(), 999L, itemDto));
    }

    @Test
    void testUpdateItemByNonOwnerThrowsException() {
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Item description");
        item.setOwner(user);
        item = itemRepository.save(item);

        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another.user@example.com");
        userRepository.save(anotherUser);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");

        Item finalItem = item;
        assertThrows(NotFoundException.class, () -> itemService.update(anotherUser.getId(), finalItem.getId(), itemDto));
    }

    @Test
    void testDeleteItem() {
        Item item = new Item();
        item.setName("Item to delete");
        item.setDescription("Item description");
        item.setOwner(user);
        item = itemRepository.save(item);

        Boolean result = itemService.delete(item.getId());

        assertThat(result).isTrue();
        assertThat(itemRepository.findById(item.getId())).isEmpty();
    }

    @Test
    void testDeleteNonExistentItemThrowsException() {
        assertThrows(NotFoundException.class, () -> itemService.delete(999L));
    }

    @Test
    void testSearchItems() {
        Item item = new Item();
        item.setName("Searchable Item");
        item.setDescription("Searchable description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        List<ItemDto> foundItems = itemService.search("Searchable Item");

        assertThat(foundItems).hasSize(1);
        assertThat(foundItems.getFirst().getName()).isEqualTo("Searchable Item");
    }

    @Test
    void testAddComment() {
        Item item = new Item();
        item.setName("Commented Item");
        item.setDescription("Item with comment");
        item.setOwner(user);
        item.setAvailable(true);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        CommentDto addedComment = itemService.addComment(item.getId(), user.getId(), commentDto);

        assertThat(addedComment.getText()).isEqualTo("Great item!");
        assertThat(addedComment.getAuthorName()).isEqualTo(user.getName());
    }

    @Test
    void testAddCommentToNonExistentItemThrowsException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        assertThrows(NotFoundException.class, () -> itemService.addComment(999L, user.getId(), commentDto));
    }

    @Test
    void testAddCommentByUserWhoDidNotBookItemThrowsException() {
        Item item = new Item();
        item.setName("Item without booking");
        item.setDescription("Item not booked by user");
        item.setOwner(user);
        item = itemRepository.save(item);

        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another.user@example.com");
        userRepository.save(anotherUser);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Trying to comment");

        Item finalItem = item;
        assertThrows(InvalidParamException.class, () -> itemService.addComment(finalItem.getId(), anotherUser.getId(), commentDto));
    }
}
