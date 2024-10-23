package booking;

import org.junit.jupiter.api.Test;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.mapper.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;
import ru.practicum.server.booking.Status;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private static final Long BOOKING_ID = 1L;
    private static final LocalDateTime START_TIME = LocalDateTime.now();
    private static final LocalDateTime END_TIME = LocalDateTime.now().plusDays(1);
    private static final Long ITEM_ID = 2L;
    private static final Long BOOKER_ID = 3L;

    @Test
    void toBookingDto_shouldConvertBookingToBookingDto() {
        User booker = new User(BOOKER_ID, "booker@example.com", "Booker");
        Item item = Item.builder()
                .id(ITEM_ID)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(booker)
                .lastBooking(Instant.now())
                .nextBooking(Instant.now())
                .comments(Collections.emptyList())
                .build();
        Booking booking = Booking.builder()
                .id(BOOKING_ID)
                .start(START_TIME)
                .end(END_TIME)
                .item(item)
                .booker(booker)
                .owner(item.getOwner())
                .status(Status.APPROVED)
                .available(true)
                .build();

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertNotNull(bookingDto);
        assertEquals(BOOKING_ID, bookingDto.getId());
        assertEquals(START_TIME, bookingDto.getStart());
        assertEquals(END_TIME, bookingDto.getEnd());
        assertEquals(ITEM_ID, bookingDto.getItemId());
        assertEquals(BOOKER_ID, bookingDto.getBookerId());
        assertEquals(Status.APPROVED, bookingDto.getStatus());
        assertTrue(bookingDto.getApproved() == true);
    }

    @Test
    void toBookingDto_shouldReturnNullWhenBookingIsNull() {

        BookingDto bookingDto = BookingMapper.toBookingDto(null);

        assertNull(bookingDto);
    }

    @Test
    void toBooking_shouldConvertBookingDtoToBooking() {

        User booker = new User(BOOKER_ID, "booker@example.com", "Booker");
        Item item = Item.builder()
                .id(ITEM_ID)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(booker)
                .lastBooking(Instant.now())
                .nextBooking(Instant.now())
                .comments(Collections.emptyList())
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(BOOKING_ID)
                .start(START_TIME)
                .end(END_TIME)
                .build();

        Booking booking = BookingMapper.toBooking(bookingDto, booker, item);

        assertNotNull(booking);
        assertEquals(BOOKING_ID, booking.getId());
        assertEquals(START_TIME, booking.getStart());
        assertEquals(END_TIME, booking.getEnd());
        assertEquals(Status.WAITING, booking.getStatus());
        assertFalse(booking.getAvailable() == true);
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
    }

    @Test
    void toBooking_shouldReturnNullWhenBookingDtoOrUserOrItemIsNull() {

        Booking booking1 = BookingMapper.toBooking(null, null, null);
        Booking booking2 = BookingMapper.toBooking(new BookingDto(), null, null);
        Booking booking3 = BookingMapper.toBooking(null, new User(), new Item());

        assertNull(booking1);
        assertNull(booking2);
        assertNull(booking3);
    }

    @Test
    void toBookingDtoList_shouldConvertListOfBookingsToListOfBookingDtos() {

        User booker = new User(BOOKER_ID, "booker@example.com", "Booker");
        Item item = Item.builder()
                .id(ITEM_ID)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(booker)
                .lastBooking(Instant.now())
                .nextBooking(Instant.now())
                .comments(Collections.emptyList())
                .build();
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(START_TIME)
                .end(END_TIME)
                .item(item)
                .booker(booker)
                .owner(item.getOwner())
                .status(Status.APPROVED)
                .available(false)
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(START_TIME)
                .end(END_TIME)
                .item(item)
                .booker(booker)
                .owner(item.getOwner())
                .status(Status.WAITING)
                .available(false)
                .build();

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingDto> bookingDtos = BookingMapper.toBookingDtoList(bookings);

        assertEquals(2, bookingDtos.size());
        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(2L, bookingDtos.get(1).getId());
    }

    @Test
    void toBookingDtoList_shouldReturnEmptyListWhenBookingsAreEmpty() {
        List<Booking> bookings = Collections.emptyList();

        List<BookingDto> bookingDtos = BookingMapper.toBookingDtoList(bookings);

        assertTrue(bookingDtos.isEmpty());
    }
}