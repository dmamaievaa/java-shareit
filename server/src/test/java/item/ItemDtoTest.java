package item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.server.item.dto.ItemDto;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {ItemDto.class})
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoTest {

    @Autowired
    private final JacksonTester<ItemDto> json;

    @Test
    void testItemDtoSerialization() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("This is a test item")
                .available(true)
                .owner(100L)
                .request(null)
                .lastBooking("2024-10-21T10:00:00")
                .nextBooking("2024-10-22T10:00:00")
                .comments(Collections.emptyList())
                .requestId(null)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test Item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("This is a test item");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(100);
    }
}