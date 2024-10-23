package item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.item.controller.ItemController;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.utils.GlobalConstants;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = {ItemController.class})
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Test
    void shouldCreateItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        when(itemService.create(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(GlobalConstants.USERID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        ItemDto updatedItem = ItemDto.builder()
                .id(1L)
                .name("Updated name")
                .description("Updated description")
                .available(true)
                .build();

        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(updatedItem);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(GlobalConstants.USERID_HEADER, 1L)
                        .content(mapper.writeValueAsString(updatedItem))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItem.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updatedItem.getName())))
                .andExpect(jsonPath("$.description", is(updatedItem.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItem.getAvailable())));
    }

    @Test
    void shouldGetItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        when(itemService.get(anyLong())).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header(GlobalConstants.USERID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        when(itemService.delete(anyLong())).thenReturn(true);

        mvc.perform(delete("/items/{itemId}", 1L)
                        .header(GlobalConstants.USERID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldFindAllItems() throws Exception {
        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("Item1")
                .description("Description1")
                .available(true)
                .build();

        ItemDto itemDto2 = ItemDto.builder()
                .id(2L)
                .name("Item2")
                .description("Description2")
                .available(true)
                .build();

        when(itemService.findAll(anyLong())).thenReturn(List.of(itemDto1, itemDto2));

        mvc.perform(get("/items")
                        .header(GlobalConstants.USERID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId().intValue())));
    }

    @Test
    void shouldSearchItems() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Searched item")
                .description("Searched description")
                .available(true)
                .build();

        when(itemService.search(eq("search"))).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "search")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())));
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Nice item")
                .created(Instant.now())
                .build();

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(GlobalConstants.USERID_HEADER, 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId().intValue())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    void shouldReturnBadRequestForInvalidItem() throws Exception {
        ItemDto invalidItem = ItemDto.builder()
                .name("")
                .description("")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .header(GlobalConstants.USERID_HEADER, 1L)
                        .content(mapper.writeValueAsString(invalidItem))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyListForEmptySearchQuery() throws Exception {
        when(itemService.search(anyString())).thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }
}