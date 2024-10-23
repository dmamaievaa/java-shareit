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

}

