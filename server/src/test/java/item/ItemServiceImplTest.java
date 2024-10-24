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
import ru.practicum.server.booking.Status;
import ru.practicum.server.exception.InvalidParamException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.CommentRepository;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.item.service.ItemServiceImpl;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.server.utils.GlobalConstants.REQUEST_NOT_FOUND;

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

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;

    private User owner;

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
        item1.setAvailable(true);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Item description 2");
        item2.setOwner(user);
        item2.setAvailable(true);
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
        itemDto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.create(999L, itemDto));
    }

    @Test
    void testUpdateItem() {
        Item item = new Item();
        item.setName("Old Item");
        item.setDescription("Old description");
        item.setOwner(user);
        item.setAvailable(true);
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
        itemDto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.update(user.getId(), 999L, itemDto));
    }

    @Test
    void testUpdateItemByNonOwnerThrowsException() {
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Item description");
        item.setOwner(user);
        item.setAvailable(true);
        item = itemRepository.save(item);

        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another.user@example.com");
        userRepository.save(anotherUser);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setAvailable(true);

        Item finalItem = item;
        assertThrows(NotFoundException.class, () -> itemService.update(anotherUser.getId(), finalItem.getId(), itemDto));
    }

    @Test
    void testGetItem() {
        Item item = new Item();
        item.setName("Item to get");
        item.setDescription("Description for item to get");
        item.setOwner(user);
        item.setAvailable(true);
        item = itemRepository.save(item);

        ItemDto foundItem = itemService.get(item.getId());

        assertThat(foundItem.getName()).isEqualTo("Item to get");
        assertThat(foundItem.getDescription()).isEqualTo("Description for item to get");
    }

    @Test
    void testGetNonExistentItemThrowsException() {
        assertThrows(NotFoundException.class, () -> itemService.get(999L));
    }

    @Test
    void testDeleteItem() {
        Item item = new Item();
        item.setName("Item to delete");
        item.setDescription("Item description");
        item.setOwner(user);
        item.setAvailable(true);
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
        owner = new User();
        owner.setName("Test owner");
        owner.setEmail("test.owner@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Commented Item");
        item.setDescription("Item with comment");
        item.setOwner(owner);
        item.setAvailable(true);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setOwner(owner);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(Status.APPROVED);
        booking.setAvailable(true);
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
    void testAddCommentForBookedItem() {
        Item item = new Item();
        item.setName("Item for booked comment");
        item.setDescription("Item for comment test");
        item.setOwner(user);
        item.setAvailable(true);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setOwner(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(Status.APPROVED);
        booking.setAvailable(true);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Excellent item!");

        CommentDto addedComment = itemService.addComment(item.getId(), user.getId(), commentDto);

        assertThat(addedComment.getText()).isEqualTo("Excellent item!");
        assertThat(addedComment.getAuthorName()).isEqualTo(user.getName());
    }


    @Test
    void testAddCommentByUserWhoDidNotBookItemThrowsException() {
        Item item = new Item();
        item.setName("Item without booking");
        item.setDescription("Item not booked by user");
        item.setOwner(user);
        item.setAvailable(true);
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

    @Test
    void testGetComments() {
        Item item = new Item();
        item.setName("Item with comments");
        item.setDescription("Description for item with comments");
        item.setOwner(user);
        item.setAvailable(true);
        item = itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("Nice item!");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(Instant.now());
        commentRepository.save(comment);

        List<CommentDto> comments = itemService.getComments(item.getId());

        assertThat(comments).hasSize(1);
        assertThat(comments.getFirst().getText()).isEqualTo("Nice item!");
    }

    @Test
    void testGetCommentsForNonExistentItem() {
        List<CommentDto> comments = itemService.getComments(999L);
        assertThat(comments).isEmpty();
    }

    @Test
    void testCreateItemWithRequestId() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request for an item");
        itemRequest.setRequestor(user);
        itemRequest = itemRequestRepository.save(itemRequest);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item with Request");
        itemDto.setDescription("Item description with request");
        itemDto.setAvailable(true);
        itemDto.setRequestId(itemRequest.getId());

        ItemDto createdItem = itemService.create(user.getId(), itemDto);

        assertThat(createdItem.getName()).isEqualTo("New Item with Request");
        assertThat(createdItem.getDescription()).isEqualTo("Item description with request");
        assertThat(createdItem.getAvailable()).isTrue();
        assertThat(createdItem.getRequestId()).isEqualTo(itemRequest.getId());

        Item itemFromDb = itemRepository.findById(createdItem.getId()).orElseThrow();
        assertThat(itemFromDb.getRequest()).isNotNull();
        assertThat(itemFromDb.getRequest().getId()).isEqualTo(itemRequest.getId());
    }

    @Test
    void testCreateItemWithNonExistentRequestIdThrowsException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item with Non-Existent Request");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(999L);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.create(user.getId(), itemDto));
        assertThat(exception.getMessage()).isEqualTo(REQUEST_NOT_FOUND);
    }
}