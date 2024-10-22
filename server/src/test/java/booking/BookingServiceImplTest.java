package booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.service.BookingServiceImpl;
import ru.practicum.server.enums.Status;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    private final BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@gmail.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@gmail.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setOwner(owner);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);
    }

    @Test
    void testCreateBookingSuccess() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());

        BookingDto createdBooking = bookingService.createBooking(bookingDto, booker.getId());

        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getBookerId()).isEqualTo(booker.getId());
        assertThat(createdBooking.getItemId()).isEqualTo(item.getId());
    }

    @Test
    void testApproveBookingSuccess() {
        BookingDto approvedBooking = bookingService.approveBooking(booking.getId(), true, owner.getId());

        assertThat(approvedBooking.getStatus()).isEqualTo(Status.APPROVED);

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void testRejectBookingSuccess() {
        BookingDto rejectedBooking = bookingService.approveBooking(booking.getId(), false, owner.getId());

        assertThat(rejectedBooking.getStatus()).isEqualTo(Status.REJECTED);

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void testGetBookingByIdSuccess() {
        BookingDto fetchedBooking = bookingService.getBookingById(booking.getId());

        assertThat(fetchedBooking).isNotNull();
        assertThat(fetchedBooking.getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetBookingsForCurrentUserSuccess() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingService.createBooking(bookingDto, booker.getId());

        List<BookingDto> bookings = bookingService.getBookingsForCurrentUser(booker.getId(), "ALL");

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.getFirst().getBookerId()).isEqualTo(booker.getId());
    }

    @Test
    void testGetBookingsForOwnerSuccess() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingService.createBooking(bookingDto, booker.getId());

        List<BookingDto> bookings = bookingService.getBookingsForOwner(owner.getId(), "ALL");

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.getFirst().getItemId()).isEqualTo(item.getId());
    }
}