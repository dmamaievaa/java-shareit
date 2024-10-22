package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.booking.controller.BookingController;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.enums.Status;

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
@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    void shouldCreateBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 10, 22, 10, 10, 10));
        bookingDto.setEnd(LocalDateTime.of(2024, 11, 22, 11, 11, 11));
        bookingDto.setItemId(1L);

        when(bookingService.createBooking(any(BookingDto.class), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())));
    }

    @Test
    void shouldApproveBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 10, 22, 10, 10, 10));
        bookingDto.setEnd(LocalDateTime.of(2024, 11, 22, 11, 11, 11));
        bookingDto.setStatus(Status.APPROVED);

        when(bookingService.approveBooking(anyLong(), eq(true), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void shouldReturnBookingById() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 10, 22, 10, 10, 10));
        bookingDto.setEnd(LocalDateTime.of(2024, 11, 22, 11, 11, 11));

        when(bookingService.getBookingById(anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())));
    }

    @Test
    void shouldReturnBookingsForCurrentUser() throws Exception {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.of(2024, 10, 22, 10, 10, 10));
        bookingDto1.setEnd(LocalDateTime.of(2024, 11, 22, 11, 11, 11));

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.of(2024, 12, 22, 12, 12, 12));
        bookingDto2.setEnd(LocalDateTime.of(2025, 1, 23, 1, 1, 1));

        List<BookingDto> bookings = List.of(bookingDto1, bookingDto2);

        when(bookingService.getBookingsForCurrentUser(anyLong(), eq("ALL")))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class));
    }

    @Test
    void shouldReturnBookingsForOwner() throws Exception {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.of(2024, 10, 22, 10, 10, 10));
        bookingDto1.setEnd(LocalDateTime.of(2024, 11, 22, 11, 11, 11));

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.of(2024, 12, 22, 12, 12, 12));
        bookingDto2.setEnd(LocalDateTime.of(2025, 1, 23, 1, 1, 1));

        List<BookingDto> bookings = List.of(bookingDto1, bookingDto2);

        when(bookingService.getBookingsForOwner(anyLong(), eq("ALL")))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class));
    }
}