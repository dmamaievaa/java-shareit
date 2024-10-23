package item;

import org.junit.jupiter.api.Test;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private static final Long ITEM_ID = 1L;
    private static final String ITEM_NAME = "Item 1";
    private static final String ITEM_DESCRIPTION = "Description 1";
    private static final Boolean ITEM_AVAILABLE = true;
    private static final Long OWNER_ID = 2L;
    private static final Long COMMENT_ID = 3L;
    private static final String COMMENT_TEXT = "Comment text";
    private static final Instant COMMENT_CREATED = Instant.now();

    @Test
    void toItemDto_shouldConvertItemToItemDto() {
        User owner = new User(OWNER_ID, "owner@example.com", "Owner");
        Item item = Item.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCRIPTION)
                .available(ITEM_AVAILABLE)
                .owner(owner)
                .lastBooking(Instant.now())
                .nextBooking(Instant.now())
                .comments(Collections.emptyList())
                .build();

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(ITEM_ID, itemDto.getId());
        assertEquals(ITEM_NAME, itemDto.getName());
        assertEquals(ITEM_DESCRIPTION, itemDto.getDescription());
        assertEquals(ITEM_AVAILABLE, itemDto.getAvailable());
        assertEquals(OWNER_ID, itemDto.getOwner());
        assertNull(itemDto.getRequest());
    }

    @Test
    void toItemDto_shouldReturnNull_whenItemIsNull() {
        ItemDto itemDto = ItemMapper.toItemDto(null);
        assertNull(itemDto);
    }

    @Test
    void toItem_shouldConvertItemDtoToItem() {
        ItemDto itemDto = ItemDto.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCRIPTION)
                .available(ITEM_AVAILABLE)
                .owner(OWNER_ID)
                .build();

        User owner = new User(OWNER_ID, "owner@example.com", "Owner");

        Item item = ItemMapper.toItem(itemDto, owner);

        assertNotNull(item);
        assertEquals(ITEM_ID, item.getId());
        assertEquals(ITEM_NAME, item.getName());
        assertEquals(ITEM_DESCRIPTION, item.getDescription());
        assertEquals(ITEM_AVAILABLE, item.getAvailable());
        assertEquals(owner, item.getOwner());
    }

    @Test
    void toItem_shouldReturnNull_whenItemDtoIsNull() {
        Item item = ItemMapper.toItem(null, null);
        assertNull(item);
    }

    @Test
    void toCommentDtoList_shouldConvertCommentListToCommentDtoList() {
        User author = new User(1L, "Author", "author@example.com");
        Comment comment = Comment.builder()
                .id(COMMENT_ID)
                .text(COMMENT_TEXT)
                .author(author)
                .created(COMMENT_CREATED)
                .build();

        List<Comment> comments = List.of(comment);

        List<CommentDto> commentDtos = ItemMapper.toCommentDtoList(comments);

        assertNotNull(commentDtos);
        assertEquals(1, commentDtos.size());
        assertEquals(COMMENT_ID, commentDtos.getFirst().getId());
        assertEquals(COMMENT_TEXT, commentDtos.getFirst().getText());
        assertEquals("Author", commentDtos.getFirst().getAuthorName());
        assertEquals(COMMENT_CREATED, commentDtos.getFirst().getCreated());
    }

    @Test
    void toCommentDtoList_shouldReturnEmptyList_whenCommentsAreNull() {
        List<CommentDto> commentDtos = ItemMapper.toCommentDtoList(null);
        assertNotNull(commentDtos);
        assertTrue(commentDtos.isEmpty());
    }

    @Test
    void toCommentDtoList_shouldReturnEmptyList_whenCommentsAreEmpty() {
        List<CommentDto> commentDtos = ItemMapper.toCommentDtoList(Collections.emptyList());
        assertNotNull(commentDtos);
        assertTrue(commentDtos.isEmpty());
    }

    @Test
    void itemPatch_shouldUpdateCurrentItem() {
        Item currentItem = Item.builder()
                .id(ITEM_ID)
                .name("Old Name")
                .description("Old Description")
                .available(false)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .name(ITEM_NAME)
                .description(null)
                .available(true)
                .build();

        Item updatedItem = ItemMapper.itemPatch(currentItem, itemDto);

        assertNotNull(updatedItem);
        assertEquals(ITEM_NAME, updatedItem.getName());
        assertEquals("Old Description", updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
    }
}