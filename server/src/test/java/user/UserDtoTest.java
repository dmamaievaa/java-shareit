package user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.server.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {UserDto.class})
@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDtoSerialization() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test.user@gmail.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test.user@gmail.com");
    }

    @Test
    void testUserDtoDeserialization() throws Exception {
        String jsonContent = "{\"id\": 1, \"name\": \"Test User\", \"email\": \"test.user@gmail.com\"}";

        UserDto userDto = json.parseObject(jsonContent);

        assertThat(userDto.getId()).isEqualTo(1);
        assertThat(userDto.getName()).isEqualTo("Test User");
        assertThat(userDto.getEmail()).isEqualTo("test.user@gmail.com");
    }
}