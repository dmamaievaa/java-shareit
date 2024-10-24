package user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.UserAlreadyExists;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;
import ru.practicum.server.user.service.UserServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    private final UserServiceImpl userService;

    private final UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Initial User");
        user.setEmail("initial.user@example.com");
        user = userRepository.save(user);
    }

    @Test
    void testUpdateUserName() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        UserDto updatedUser = userService.update(user.getId(), updateDto);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("initial.user@example.com");
    }

    @Test
    void testUpdateUserEmail() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("updated.email@example.com");

        UserDto updatedUser = userService.update(user.getId(), updateDto);

        assertThat(updatedUser.getName()).isEqualTo("Initial User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated.email@example.com");
    }

    @Test
    void testUpdateUserNameAndEmail() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated.email@example.com");

        UserDto updatedUser = userService.update(user.getId(), updateDto);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated.email@example.com");
    }

    @Test
    void testUpdateWithExistingEmail() {
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another.email@example.com");
        userRepository.save(anotherUser);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("another.email@example.com");

        assertThatThrownBy(() -> userService.update(user.getId(), updateDto))
                .isInstanceOf(UserAlreadyExists.class)
                .hasMessageContaining("Email is already in use.");
    }

    @Test
    void testUpdateNonExistingUser() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Non-Existing User");

        assertThatThrownBy(() -> userService.update(999L, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void testDeleteExistingUser() {
        Boolean result = userService.delete(user.getId());
        assertThat(result).isTrue();
        assertThat(userRepository.existsById(user.getId())).isFalse();
    }

    @Test
    void testDeleteNonExistentUser() {
        assertThatThrownBy(() -> userService.delete(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("New User");
        userDto.setEmail("new.user@example.com");

        UserDto createdUser = userService.create(userDto);

        assertThat(createdUser.getName()).isEqualTo("New User");
        assertThat(createdUser.getEmail()).isEqualTo("new.user@example.com");
        assertThat(userRepository.existsById(createdUser.getId())).isTrue();
    }

    @Test
    void testGetExistingUser() {
        UserDto fetchedUser = userService.get(user.getId());

        assertThat(fetchedUser.getName()).isEqualTo("Initial User");
        assertThat(fetchedUser.getEmail()).isEqualTo("initial.user@example.com");
    }

    @Test
    void testGetNonExistentUserThrowsException() {
        assertThatThrownBy(() -> userService.get(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void testFindAllUsers() {
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another.user@example.com");
        userRepository.save(anotherUser);

        List<UserDto> users = userService.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserDto::getName)
                .containsExactlyInAnyOrder("Initial User", "Another User");
    }

    @Test
    void testFindAllNoUsers() {
        userRepository.deleteAll();
        List<UserDto> users = userService.findAll();

        assertThat(users).isEmpty();
    }
}
