package item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.item.ItemClient;
import ru.practicum.gateway.item.ItemController;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.item.dto.CommentDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = {ItemController.class})
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Test
    public void shouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = new ItemDto("Item 1", "Description 1", true, null);
        Long ownerId = 1L;

        Mockito.when(itemClient.create(ownerId, itemDto))
                .thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item 1\", \"description\":\"Description 1\", \"available\":true}")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Item 1")))
                .andExpect(jsonPath("$.description", is("Description 1")))
                .andExpect(jsonPath("$.available", is(true)));

        Mockito.verify(itemClient, Mockito.times(1)).create(ownerId, itemDto);
    }

    @Test
    public void shouldReturnUpdatedItem() throws Exception {
        ItemDto updatedItem = new ItemDto("Updated Item", "Updated Description", true, null);
        Long itemId = 1L;
        Long ownerId = 1L;

        Mockito.when(itemClient.update(itemId, ownerId, updatedItem))
                .thenReturn(ResponseEntity.ok(updatedItem));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Item\", \"description\":\"Updated Description\", \"available\":true}")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Item")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.available", is(true)));

        Mockito.verify(itemClient, Mockito.times(1)).update(itemId, ownerId, updatedItem);
    }

    @Test
    public void shouldReturnItemList() throws Exception {
        Long ownerId = 1L;
        ItemDto itemDto1 = new ItemDto("Item 1", "Description 1", true, null);
        ItemDto itemDto2 = new ItemDto("Item 2", "Description 2", false, null);

        Mockito.when(itemClient.findAll(ownerId))
                .thenReturn(ResponseEntity.ok(List.of(itemDto1, itemDto2)));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[1].name", is("Item 2")));

        Mockito.verify(itemClient, Mockito.times(1)).findAll(ownerId);
    }

    @Test
    public void shouldReturnFoundItems() throws Exception {
        Long userId = 1L;
        String searchText = "Item";
        ItemDto itemDto1 = new ItemDto("Item 1", "Description 1", true, null);
        ItemDto itemDto2 = new ItemDto("Item 2", "Description 2", true, null);

        Mockito.when(itemClient.search(userId, searchText))
                .thenReturn(ResponseEntity.ok(List.of(itemDto1, itemDto2)));

        mockMvc.perform(get("/items/search")
                        .param("text", searchText)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[1].name", is("Item 2")));

        Mockito.verify(itemClient, Mockito.times(1)).search(userId, searchText);
    }

    @Test
    public void shouldReturnCreatedComment() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDto = new CommentDto("Great item!");

        Mockito.when(itemClient.addComment(itemId, userId, commentDto))
                .thenReturn(ResponseEntity.ok(commentDto));

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Great item!\"}")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("Great item!")));

        Mockito.verify(itemClient, Mockito.times(1)).addComment(itemId, userId, commentDto);
    }
}