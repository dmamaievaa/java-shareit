package user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.server.user.model.User;

public class UserTest {

    @Test
    void shouldBeEqual() {
        User user1 = User.builder()
                .id(1L)
                .name("user1 name")
                .email("user1@example.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("user1 name")
                .email("user1@example.com")
                .build();

        Assertions.assertEquals(user1, user2, "Users with the same id should be equal");
    }

    @Test
    void shouldNotBeEqual() {
        User user1 = User.builder()
                .id(1L)
                .name("user1 name")
                .email("user1@example.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("user1 name")
                .email("user1@example.com")
                .build();

        Assertions.assertNotEquals(user1, user2, "Users with different ids should not be equal");
    }

    @Test
    void shouldReturnCorrectHashcode() {
        User user1 = User.builder()
                .id(1L)
                .name("user1 name")
                .email("user1@example.com")
                .build();

        int expectedHashcode = user1.getId().hashCode();

        Assertions.assertEquals(expectedHashcode, user1.hashCode(),
                "Hashcode should be based on the user's id");
    }
}
