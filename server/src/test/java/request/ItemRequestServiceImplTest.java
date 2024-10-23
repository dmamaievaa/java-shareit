package request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.exception.DataNotFoundException;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.request.service.ItemRequestServiceImpl;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
        user.setEmail("test.user@gmail.com");
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

    @Test
    void testGetAllByUser() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request by user");
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);

        List<ItemRequestDto> requests = itemRequestService.getAllByUser(user.getId());

        assertThat(requests).isNotNull();
        assertThat(requests.getFirst().getDescription()).isEqualTo("Request by user");
    }

    @Test
    void testGetAll() {
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another.user@example.com");
        anotherUser = userRepository.save(anotherUser);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request from another user");
        itemRequest.setRequestor(anotherUser);
        itemRequestRepository.save(itemRequest);

        List<ItemRequestDto> requests = itemRequestService.getAll(user.getId());

        assertThat(requests).isNotNull();
        assertThat(requests.getFirst().getDescription()).isEqualTo("Request from another user");
    }

    @Test
    void testGetById() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request by id");
        itemRequest.setRequestor(user);
        itemRequest = itemRequestRepository.save(itemRequest);

        ItemRequestDto foundRequest = itemRequestService.getById(itemRequest.getId());

        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getId()).isEqualTo(itemRequest.getId());
        assertThat(foundRequest.getDescription()).isEqualTo("Request by id");
    }

    @Test
    void testGetByIdNotFound() {
        assertThatThrownBy(() -> itemRequestService.getById(999L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Request not found");
    }
}