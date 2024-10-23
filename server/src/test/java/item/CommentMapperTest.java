package item;

import org.junit.jupiter.api.Test;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.mapper.CommentMapper;
import ru.practicum.server.item.model.Comment;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private static final Long COMMENT_ID = 1L;
    private static final String COMMENT_TEXT = "This is a comment.";
    private static final Instant COMMENT_CREATED = Instant.now();

    @Test
    void toCommentDto_shouldReturnNull_whenCommentIsNull() {
        CommentDto commentDto = CommentMapper.toCommentDto(null);
        assertNull(commentDto);
    }

    @Test
    void toComment_shouldConvertCommentDtoToComment() {
        CommentDto commentDto = CommentDto.builder()
                .id(COMMENT_ID)
                .text(COMMENT_TEXT)
                .created(COMMENT_CREATED)
                .build();

        Comment comment = CommentMapper.toComment(commentDto);

        assertNotNull(comment);
        assertEquals(COMMENT_ID, comment.getId());
        assertEquals(COMMENT_TEXT, comment.getText());
        assertEquals(COMMENT_CREATED, comment.getCreated());
    }

    @Test
    void toComment_shouldReturnNull_whenCommentDtoIsNull() {
        Comment comment = CommentMapper.toComment(null);
        assertNull(comment);
    }

    @Test
    void toCommentDtoList_shouldReturnEmptyList_whenCommentsAreEmpty() {
        List<CommentDto> commentDtos = CommentMapper.toCommentDtoList(Collections.emptyList());
        assertNotNull(commentDtos);
        assertTrue(commentDtos.isEmpty());
    }
}