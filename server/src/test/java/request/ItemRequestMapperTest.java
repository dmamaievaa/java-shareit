package request;

import org.junit.jupiter.api.Test;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.mapper.ItemRequestMapper;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private static final Long REQUEST_ID = 1L;
    private static final String REQUEST_DESCRIPTION = "Need help with this item.";
    private static final Long USER_ID = 2L;
    private static final LocalDateTime REQUEST_CREATED = LocalDateTime.now();

    @Test
    void toItemRequest_shouldConvertItemRequestDtoToItemRequest() {
        User user = new User(USER_ID, "user@example.com", "User");
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(REQUEST_ID)
                .description(REQUEST_DESCRIPTION)
                .build();

        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto, user);

        assertNotNull(request);
        assertEquals(REQUEST_ID, request.getId());
        assertEquals(REQUEST_DESCRIPTION, request.getDescription());
        assertEquals(user, request.getRequestor());
        assertNotNull(request.getCreated());
    }

    @Test
    void toItemRequestDto_shouldConvertItemRequestToItemRequestDto() {
        User user = new User(USER_ID, "user@example.com", "User");
        ItemRequest request = ItemRequest.builder()
                .id(REQUEST_ID)
                .description(REQUEST_DESCRIPTION)
                .requestor(user)
                .created(REQUEST_CREATED)
                .build();

        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);

        assertNotNull(requestDto);
        assertEquals(REQUEST_ID, requestDto.getId());
        assertEquals(REQUEST_DESCRIPTION, requestDto.getDescription());
        assertEquals(REQUEST_CREATED, requestDto.getCreated());
    }
}