package request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.request.ItemRequestClient;
import ru.practicum.gateway.request.ItemRequestController;
import ru.practicum.gateway.request.dto.ItemRequestDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {ItemRequestController.class})
@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void  shouldCreateRequest() throws Exception {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto("Need a book");
        ResponseEntity<Object> response = ResponseEntity.ok(itemRequestDto);

        Mockito.when(itemRequestClient.create(Mockito.eq(userId), Mockito.any(ItemRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void  shouldReturnAllRequestsByUser() throws Exception {
        Long userId = 1L;
        ItemRequestDto itemRequestDto1 = new ItemRequestDto("Need a book");
        ItemRequestDto itemRequestDto2 = new ItemRequestDto("Looking for a car");

        ResponseEntity<Object> response = ResponseEntity.ok(List.of(itemRequestDto1, itemRequestDto2));

        Mockito.when(itemRequestClient.getAllByUser(Mockito.eq(userId)))
                .thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }

    @Test
    void  shouldReturnRequestById() throws Exception {
        Long requestId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto("Need a book");

        ResponseEntity<Object> response = ResponseEntity.ok(itemRequestDto);

        Mockito.when(itemRequestClient.getById(Mockito.eq(requestId)))
                .thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }
}
