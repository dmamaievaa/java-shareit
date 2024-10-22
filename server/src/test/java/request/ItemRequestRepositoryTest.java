package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(classes = ShareItServer.class)
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        requestor = new User(1L, "Requestor", "requestor@gmail.com");
        requestor = userRepository.save(requestor);

        request1 = ItemRequest.builder()
                .description("Request 1")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        request1 = itemRequestRepository.save(request1);

        request2 = ItemRequest.builder()
                .description("Request 2")
                .requestor(requestor)
                .created(LocalDateTime.now().plusSeconds(10))
                .build();
        request2 = itemRequestRepository.save(request2);
    }

    @Test
    void testFindAllByRequestorIdOrderByCreatedAsc() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(requestor.getId());

        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertEquals(request1.getId(), requests.get(0).getId());
        assertEquals(request2.getId(), requests.get(1).getId());
    }

    @Test
    void testFindAllByRequestorIdOrderByCreatedAsc_NoRequests() {
        User anotherUser = new User(2L, "User", "user@gmail.com");
        userRepository.save(anotherUser);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(anotherUser.getId());

        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }
}
