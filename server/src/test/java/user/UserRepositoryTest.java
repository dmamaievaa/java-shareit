package user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(classes = ShareItServer.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test User", "testuser@gmail.com");
        userRepository.save(user);
    }

    @Test
    void testUserExistsByEmail() {
        boolean exists = userRepository.existsByEmail("testuser@gmail.com");
        assertTrue(exists, "User should exist for the specified email");
    }

    @Test
    void testUserNotExistsByEmail() {
        boolean exists = userRepository.existsByEmail("nonexistent@gmail.com");
        assertFalse(exists, "User should not exist for the specified email");
    }
}
