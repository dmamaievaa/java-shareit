package user;

import org.junit.jupiter.api.Test;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Doe";
    private static final String USER_EMAIL = "john.doe@example.com";

    @Test
    void toUserDto_shouldConvertUserToUserDto() {

        User user = User.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        UserDto userDto = UserMapper.toUserDto(user);

        assertNotNull(userDto);
        assertEquals(USER_ID, userDto.getId());
        assertEquals(USER_NAME, userDto.getName());
        assertEquals(USER_EMAIL, userDto.getEmail());
    }

    @Test
    void toUserDto_shouldReturnNull_whenUserIsNull() {

        UserDto userDto = UserMapper.toUserDto(null);

        assertNull(userDto);
    }

    @Test
    void toUser_shouldConvertUserDtoToUser() {

        UserDto userDto = UserDto.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        User user = UserMapper.toUser(userDto);

        assertNotNull(user);
        assertEquals(USER_ID, user.getId());
        assertEquals(USER_NAME, user.getName());
        assertEquals(USER_EMAIL, user.getEmail());
    }

    @Test
    void toUser_shouldReturnNull_whenUserDtoIsNull() {

        User user = UserMapper.toUser(null);

        assertNull(user);
    }

    @Test
    void toUserDtoList_shouldConvertUserListToUserDtoList() {

        User user1 = User.builder().id(1L).name("User 1").email("user1@example.com").build();
        User user2 = User.builder().id(2L).name("User 2").email("user2@example.com").build();
        List<User> users = List.of(user1, user2);

        List<UserDto> userDtos = UserMapper.toUserDtoList(users);

        assertNotNull(userDtos);
        assertEquals(2, userDtos.size());
        assertEquals(user1.getId(), userDtos.getFirst().getId());
        assertEquals(user1.getName(), userDtos.get(0).getName());
        assertEquals(user1.getEmail(), userDtos.get(0).getEmail());
        assertEquals(user2.getId(), userDtos.get(1).getId());
        assertEquals(user2.getName(), userDtos.get(1).getName());
        assertEquals(user2.getEmail(), userDtos.get(1).getEmail());
    }

    @Test
    void toUserDtoList_shouldReturnEmptyList_whenUsersAreEmpty() {

        List<UserDto> userDtos = UserMapper.toUserDtoList(Collections.emptyList());

        assertNotNull(userDtos);
        assertTrue(userDtos.isEmpty());
    }
}