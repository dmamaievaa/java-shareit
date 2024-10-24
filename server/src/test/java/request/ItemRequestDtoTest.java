package request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {ItemRequestDto.class})
@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDtoSerialization() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 10, 23, 10, 0, 0);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test request")
                .created(created)
                .items(Collections.singletonList(ItemDto.builder().id(100L).name("Test Item").build()))
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test request");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-10-23T10:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(100);
    }

    @Test
    void testItemRequestDtoDeserialization() throws Exception {
        String jsonContent = "{\"id\": 1, \"description\": \"Test request\", \"created\": \"2024-10-23T10:00:00\", \"items\": [{\"id\": 100, \"name\": \"Test Item\"}]}";

        ItemRequestDto itemRequestDto = json.parseObject(jsonContent);

        assertThat(itemRequestDto.getId()).isEqualTo(1);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Test request");
        assertThat(itemRequestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 10, 23, 10, 0, 0));
        assertThat(itemRequestDto.getItems()).hasSize(1);
        assertThat(itemRequestDto.getItems().getFirst().getId()).isEqualTo(100);
    }
}
