package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.Status;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Test User", "testUser1@gmail.com");
        owner = userRepository.save(owner);

        booker = new User(2L, "Test User 2", "testUser2@gmail.com");
        booker = userRepository.save(booker);

        item = Item.builder()
                .name("Test Item")
                .description("Item description")
                .available(true)
                .owner(owner)
                .build();
        item = itemRepository.save(item);

        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        booking = bookingRepository.save(booking);
    }

    @Test
    void testFindByItemIdAndUserIdAndEndBookingBefore() {
        LocalDateTime now = LocalDateTime.now();
        Optional<Booking> foundBooking = bookingRepository.findByItemIdAndUserIdAndEndBookingBefore(item.getId(), booker.getId(), now.plusDays(2));

        assertTrue(foundBooking.isPresent());
        assertEquals(booking.getId(), foundBooking.get().getId());
    }

    @Test
    void testFindByItemIdAndUserIdAndEndBookingBeforeNotFound() {
        LocalDateTime now = LocalDateTime.now();
        Optional<Booking> foundBooking = bookingRepository.findByItemIdAndUserIdAndEndBookingBefore(item.getId(), booker.getId(), now.minusDays(1));

        assertFalse(foundBooking.isPresent());
    }

    @Test
    void testFindByBookerAndStatusNotFound() {
        List<Booking> bookings = bookingRepository.findByBookerAndStatus(booker.getId(), Status.APPROVED);

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testFindWaitingForCurrentUser() {
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findWaitingForCurrentUser(booker.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty(), "Expected bookings to not be empty, but it was empty.");

        assertTrue(bookings.stream().allMatch(b -> b.getStatus() == Status.WAITING),
                "Some bookings do not have the expected status 'WAITING'.");
    }

    @Test
    void testFindCancelledForCurrentUser() {
        booking.setStatus(Status.CANCELED);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findCancelledForCurrentUser(booker.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.CANCELED, bookings.getFirst().getStatus());
    }

    @Test
    void testFindApprovedForCurrentUser() {
        booking.setStatus(Status.APPROVED);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findApprovedForCurrentUser(booker.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.APPROVED, bookings.getFirst().getStatus());
    }

    @Test
    void testFindRejectedForCurrentUser() {
        booking.setStatus(Status.REJECTED);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findRejectedForCurrentUser(booker.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.REJECTED, bookings.getFirst().getStatus());
    }

    @Test
    void testFindCurrentForCurrentUser() {
        booking.setStatus(Status.CURRENT);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findCurrentForCurrentUser(booker.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.CURRENT, bookings.getFirst().getStatus());
    }

    @Test
    void testFindPastForCurrentUser() {
        booking.setStatus(Status.PAST);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findPastForCurrentUser(booker.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.PAST, bookings.getFirst().getStatus());
    }

    @Test
    void testFindFutureForCurrentUser() {
        booking.setStatus(Status.FUTURE);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findFutureForCurrentUser(booker.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.FUTURE, bookings.getFirst().getStatus());
    }

    @Test
    void testFindWaitingForOwnerId() {
        List<Booking> bookings = bookingRepository.findWaitingForOwnerId(owner.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.WAITING, bookings.getFirst().getStatus());
    }

    @Test
    void testFindRejectedForOwnerId() {
        booking.setStatus(Status.REJECTED);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findRejectedForOwnerId(owner.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.REJECTED, bookings.getFirst().getStatus());
    }

    @Test
    void testFindApprovedForOwnerId() {
        booking.setStatus(Status.APPROVED);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findApprovedForOwnerId(owner.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.APPROVED, bookings.getFirst().getStatus());
    }

    @Test
    void testFindCancelledForOwnerId() {
        booking.setStatus(Status.CANCELED);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findCancelledForOwnerId(owner.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.CANCELED, bookings.getFirst().getStatus());
    }

    @Test
    void testFindCurrentForOwnerId() {
        booking.setStatus(Status.CURRENT);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findCurrentForOwnerId(owner.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.CURRENT, bookings.getFirst().getStatus());
    }

    @Test
    void testFindPastForOwnerId() {
        booking.setStatus(Status.PAST);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findPastForOwnerId(owner.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.PAST, bookings.getFirst().getStatus());
    }

    @Test
    void testFindFutureForOwnerId() {
        booking.setStatus(Status.FUTURE);
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findFutureForOwnerId(owner.getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(Status.FUTURE, bookings.getFirst().getStatus());
    }
}

