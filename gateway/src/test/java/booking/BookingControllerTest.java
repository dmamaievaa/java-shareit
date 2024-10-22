package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.booking.BookingClient;
import ru.practicum.gateway.booking.BookingController;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.BookingState;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {BookingController.class})
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void shouldCreateBooking() throws Exception {
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2024, 11, 5, 10, 0),
                LocalDateTime.of(2024, 11, 6, 10, 0));

        ResponseEntity<Object> response = ResponseEntity.ok(bookingDto);

        when(bookingClient.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(response);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is("2024-11-05T10:00:00")))
                .andExpect(jsonPath("$.end", is("2024-11-06T10:00:00")));
    }

    @Test
    void  shouldApproveBooking() throws Exception {
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2024, 11, 5, 10, 0),
                LocalDateTime.of(2024, 11, 6, 10, 0));

        ResponseEntity<Object> response = ResponseEntity.ok(bookingDto);

        when(bookingClient.approveBooking(anyLong(), anyLong(), eq(true)))
                .thenReturn(response);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is("2024-11-05T10:00:00")));
    }

    @Test
    void  shouldGetBooking() throws Exception {
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2024, 11, 5, 10, 0),
                LocalDateTime.of(2024, 11, 6, 10, 0));

        ResponseEntity<Object> response = ResponseEntity.ok(bookingDto);

        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(response);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is("2024-11-05T10:00:00")));

    }

    @Test
    void  shouldGetBookingList() throws Exception {
        BookingDto bookingDto1 = new BookingDto(1L,
                LocalDateTime.of(2024, 11, 5, 10, 0),
                LocalDateTime.of(2024, 11, 6, 10, 0));

        BookingDto bookingDto2 = new BookingDto(2L,
                LocalDateTime.of(2024, 11, 7, 10, 0),
                LocalDateTime.of(2024, 11, 8, 10, 0));

        List<BookingDto> bookingList = List.of(bookingDto1, bookingDto2);
        ResponseEntity<Object> response = ResponseEntity.ok(bookingList);

        when(bookingClient.getBookings(anyLong(), eq(BookingState.ALL), any(), any()))
                .thenReturn(response);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$[1].itemId", is(bookingDto2.getItemId()), Long.class));
    }

    @Test
    void  shouldGetOwnerBookings() throws Exception {
        BookingDto bookingDto1 = new BookingDto(1L,
                LocalDateTime.of(2024, 11, 5, 10, 0),
                LocalDateTime.of(2024, 11, 6, 10, 0));

        BookingDto bookingDto2 = new BookingDto(2L,
                LocalDateTime.of(2024, 11, 7, 10, 0),
                LocalDateTime.of(2024, 11, 8, 10, 0));

        List<BookingDto> bookingList = List.of(bookingDto1, bookingDto2);
        ResponseEntity<Object> response = ResponseEntity.ok(bookingList);

        when(bookingClient.getOwnerBookings(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$[1].itemId", is(bookingDto2.getItemId()), Long.class));
    }
}

