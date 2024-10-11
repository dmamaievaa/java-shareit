package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.handler.AllBookingHandler;
import ru.practicum.shareit.booking.handler.ApprovedBookingHandler;
import ru.practicum.shareit.booking.handler.BookingHandler;
import ru.practicum.shareit.booking.handler.CancelledBookingHandler;
import ru.practicum.shareit.booking.handler.CurrentBookingHandler;
import ru.practicum.shareit.booking.handler.FutureBookingHandler;
import ru.practicum.shareit.booking.handler.PastBookingHandler;
import ru.practicum.shareit.booking.handler.RejectedBookingHandler;
import ru.practicum.shareit.booking.handler.WaitingBookingHandler;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.InvalidParamException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

import static ru.practicum.shareit.utils.GlobalConstants.BOOKING_NOT_FOUND;
import static ru.practicum.shareit.utils.GlobalConstants.INVALID_STATE;
import static ru.practicum.shareit.utils.GlobalConstants.ITEM_NOT_AVAILABLE;
import static ru.practicum.shareit.utils.GlobalConstants.ITEM_NOT_FOUND;
import static ru.practicum.shareit.utils.GlobalConstants.USER_NOT_FOUND;
import static ru.practicum.shareit.utils.GlobalConstants.USER_NOT_OWNER;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private BookingHandler buildBookingHandlerChain() {
        AllBookingHandler allHandler = new AllBookingHandler(bookingRepository);
        ApprovedBookingHandler approvedHandler = new ApprovedBookingHandler(bookingRepository);
        CancelledBookingHandler cancelledHandler = new CancelledBookingHandler(bookingRepository);
        CurrentBookingHandler currentHandler = new CurrentBookingHandler(bookingRepository);
        FutureBookingHandler futureHandler = new FutureBookingHandler(bookingRepository);
        PastBookingHandler pastHandler = new PastBookingHandler(bookingRepository);
        RejectedBookingHandler rejectedHandler = new RejectedBookingHandler(bookingRepository);
        WaitingBookingHandler waitingHandler = new WaitingBookingHandler(bookingRepository);

        allHandler.setNext(approvedHandler);
        approvedHandler.setNext(cancelledHandler);
        cancelledHandler.setNext(currentHandler);
        currentHandler.setNext(futureHandler);
        futureHandler.setNext(pastHandler);
        pastHandler.setNext(rejectedHandler);
        rejectedHandler.setNext(waitingHandler);

        return allHandler;
    }

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

    private void checkItemAvailability(Item item) {
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

        BookingHandler handler = buildBookingHandlerChain();
        do {
            if (handler.canHandle(status)) {
                List<Booking> bookings = handler.handle(userId, isOwner);
                return BookingMapper.toBookingDtoList(bookings);
            }
            handler = handler.getNext();
        } while (handler != null);

        throw new InvalidParamException(INVALID_STATE, state);
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
