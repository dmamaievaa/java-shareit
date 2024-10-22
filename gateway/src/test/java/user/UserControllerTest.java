package user;

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
import ru.practicum.gateway.user.UserClient;
import ru.practicum.gateway.user.UserController;
import ru.practicum.gateway.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {UserController.class})
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void shouldCreateUser() throws Exception {
        UserDto userDto = new UserDto("Test User", "testUser@gmail.com");
        ResponseEntity<Object> response = ResponseEntity.ok(userDto);

        Mockito.when(userClient.create(Mockito.any(UserDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void shouldFindAllUsers() throws Exception {
        UserDto user1 = new UserDto("Test User", "testUser@gmail.com");
        UserDto user2 = new UserDto("Test User2", "testUser2@gmail.com");
        ResponseEntity<Object> response = ResponseEntity.ok(List.of(user1, user2));

        Mockito.when(userClient.findAll()).thenReturn(response);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail())));
    }

    @Test
    void shouldGetUserById() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto("Test User", "testUser@gmail.com");
        ResponseEntity<Object> response = ResponseEntity.ok(userDto);

        Mockito.when(userClient.get(Mockito.eq(userId))).thenReturn(response);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto("Test User", "testUser@gmail.com");
        ResponseEntity<Object> response = ResponseEntity.ok(userDto);

        Mockito.when(userClient.update(Mockito.eq(userId), Mockito.any(UserDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        Mockito.when(userClient.delete(Mockito.eq(userId))).thenReturn(response);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
    }
}

