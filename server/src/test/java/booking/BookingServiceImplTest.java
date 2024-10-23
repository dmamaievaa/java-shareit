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
import ru.practicum.server.booking.handler.AllBookingHandler;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.service.BookingServiceImpl;
import ru.practicum.server.booking.Status;
import ru.practicum.server.exception.InvalidParamException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.server.utils.GlobalConstants.BOOKING_NOT_FOUND;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    private final BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final AllBookingHandler allBookingHandler;

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
    void testApproveBookingNotFound() {
        Long nonExistentBookingId = 999L;

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> bookingService.approveBooking(nonExistentBookingId, true, owner.getId()));
        assertThat(thrown.getMessage()).contains(BOOKING_NOT_FOUND + nonExistentBookingId);
    }

    @Test
    void testCreateBookingWithNonExistentUser() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, 999L));
    }

    @Test
    void testCreateBookingWithNonExistentItem() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(999L);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, booker.getId()));
    }

    @Test
    void testCreateBookingWithUnavailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(bookingDto, booker.getId()));
    }

    @Test
    void testApproveBookingByNonOwner() {
        User notOwner = new User();
        notOwner.setName("Not Owner");
        notOwner.setEmail("notowner@gmail.com");
        notOwner = userRepository.save(notOwner);

        User finalNotOwner = notOwner;
        assertThrows(InvalidParamException.class, () -> bookingService.approveBooking(booking.getId(), true, finalNotOwner.getId()));
    }

    @Test
    void testGetBookingsWithInvalidState() {
        assertThrows(InvalidParamException.class, () -> bookingService.getBookingsForCurrentUser(booker.getId(), "INVALID_STATE"));
    }

    @Test
    void testCheckItemAvailabilitySetsItemAvailable() {

        Item unavailableItem = new Item();
        unavailableItem.setName("Unavailable Item");
        unavailableItem.setDescription("Item description");
        unavailableItem.setAvailable(false);

        Instant lastBookingTime = Instant.now().minus(1, ChronoUnit.DAYS);
        unavailableItem.setLastBooking(lastBookingTime);

        unavailableItem = itemRepository.save(unavailableItem);

        bookingService.checkItemAvailability(unavailableItem);

        Item updatedItem = itemRepository.findById(unavailableItem.getId()).orElseThrow();
        assertThat(updatedItem.getAvailable()).isTrue();
    }

    @Test
    void testRejectBookingSuccess() {
        BookingDto rejectedBooking = bookingService.approveBooking(booking.getId(), false, owner.getId());

        assertThat(rejectedBooking.getStatus()).isEqualTo(Status.REJECTED);

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void testApproveBookingRejected() {
        BookingDto rejectedBooking = bookingService.approveBooking(booking.getId(), false, owner.getId());

        assertThat(rejectedBooking.getStatus()).isEqualTo(Status.REJECTED);

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void testApproveBookingApproved() {
        BookingDto approvedBooking = bookingService.approveBooking(booking.getId(), true, owner.getId());

        assertThat(approvedBooking.getStatus()).isEqualTo(Status.APPROVED);

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void testCheckItemAvailabilityWhenAvailable() {
        item.setAvailable(true);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());

        BookingDto createdBooking = bookingService.createBooking(bookingDto, booker.getId());
        assertThat(createdBooking).isNotNull();
    }

    @Test
    void testValidateBookingOwnerThrowsExceptionForNonOwner() {
        User notOwner = new User();
        notOwner.setName("Not Owner");
        notOwner.setEmail("notowner@gmail.com");
        notOwner = userRepository.save(notOwner);

        User finalNonOwner = notOwner;
        assertThrows(InvalidParamException.class, () ->
                bookingService.approveBooking(booking.getId(), true, finalNonOwner.getId())
        );
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