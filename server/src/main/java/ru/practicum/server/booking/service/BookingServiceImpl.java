package ru.practicum.server.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.handler.AllBookingHandler;
import ru.practicum.server.booking.mapper.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.Status;
import ru.practicum.server.exception.InvalidParamException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

import static ru.practicum.server.utils.GlobalConstants.BOOKING_NOT_FOUND;
import static ru.practicum.server.utils.GlobalConstants.INVALID_STATE;
import static ru.practicum.server.utils.GlobalConstants.ITEM_NOT_AVAILABLE;
import static ru.practicum.server.utils.GlobalConstants.ITEM_NOT_FOUND;
import static ru.practicum.server.utils.GlobalConstants.USER_NOT_FOUND;
import static ru.practicum.server.utils.GlobalConstants.USER_NOT_OWNER;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final AllBookingHandler allBookingHandler;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + userId));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND + bookingDto.getItemId()));

        checkItemAvailability(item);

        bookingDto.setBookerId(userId);

        Booking booking = BookingMapper.toBooking(bookingDto, booker, item);

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(savedBooking);
    }

    public void checkItemAvailability(Item item) {
        if (!item.getAvailable()) {
            if (item.getLastBooking() != null && item.getLastBooking().isBefore(Instant.now())) {
                item.setAvailable(true);
            } else {
                throw new RuntimeException(ITEM_NOT_AVAILABLE);
            }
        }
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND + bookingId));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, boolean approved, Long userId) {
        log.info("Approving booking with ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn(BOOKING_NOT_FOUND + bookingId);
                    return new NotFoundException(BOOKING_NOT_FOUND + bookingId);
                });

        log.info("Owner: {} (ID: {})", booking.getOwner().getName(), booking.getOwner().getId());

        validateBookingOwner(booking, userId);

        booking.setAvailable(false);
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking with ID: {} has been {}", bookingId, approved ? "approved" : "rejected");
        log.info("Owner: {} (ID: {}), Booker: {} (ID: {}) approved the booking.",
                booking.getOwner().getName(), booking.getOwner().getId(),
                booking.getBooker().getName(), booking.getBooker().getId());

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND + booking.getItem().getId()));

        item.setAvailable(booking.getAvailable());
        itemRepository.save(item);

        return BookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public List<BookingDto> getBookingsForCurrentUser(Long userId, String state) {
        return getBookings(userId, state, false);
    }

    @Override
    public List<BookingDto> getBookingsForOwner(Long ownerId, String state) {
        return getBookings(ownerId, state, true);
    }

    private List<BookingDto> getBookings(Long userId, String state, boolean isOwner) {
        Status status = validateUserAndGetStatus(userId, state);

        List<Booking> bookings = allBookingHandler.handle(userId, isOwner, status);

        if (bookings == null) {
            throw new InvalidParamException(INVALID_STATE, state);
        }

        return BookingMapper.toBookingDtoList(bookings);
    }

    private void validateBookingOwner(Booking booking, Long userId) {
        User owner = booking.getOwner();
        if (!owner.getId().equals(userId)) {
            log.error("User ID {} is not the owner of the booking.", userId);
            throw new InvalidParamException(USER_NOT_OWNER, userId.toString());
        }
    }

    private Status validateUserAndGetStatus(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + userId));

        try {
            return Status.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidParamException(INVALID_STATE, state);
        }
    }
}
