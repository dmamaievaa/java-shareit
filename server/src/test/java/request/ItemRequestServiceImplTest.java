package request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.request.service.ItemRequestServiceImpl;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final ItemRequestServiceImpl itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test.user@example.com");
        user = userRepository.save(user);
    }

    @Test
    void testCreateItemRequest() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("New request")
                .build();

        ItemRequestDto createdRequest = itemRequestService.create(user.getId(), requestDto);

        assertThat(createdRequest).isNotNull();
        assertThat(createdRequest.getDescription()).isEqualTo("New request");

        List<ItemRequest> requestsInDb = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(user.getId());
        assertThat(requestsInDb.getFirst().getDescription()).isEqualTo("New request");
    }
}