package item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.server.item.dto.CommentDto;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {CommentDto.class})
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentDtoTest {

    @Autowired
    private final JacksonTester<CommentDto> json;

    @Test
    void testCommentDtoSerialization() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("This is a test comment")
                .itemId(100L)
                .authorId(200L)
                .authorName("Test Author")
                .created(Instant.now())
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("This is a test comment");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(100);
        assertThat(result).extractingJsonPathNumberValue("$.authorId").isEqualTo(200);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Test Author");
    }
}
