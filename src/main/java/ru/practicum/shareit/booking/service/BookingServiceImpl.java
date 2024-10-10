package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        User booker = userRepository.findById(bookingDto.getBookerId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            if (item.getLastBooking() != null && item.getLastBooking().isBefore(Instant.now())) {
                item.setAvailable(true);
            } else {
                throw new RuntimeException("Item is not available for booking");
            }
        }

        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .owner(item.getOwner())
                .status(Status.WAITING)
                .available(false)
                .build();

        booking.setOwner(item.getOwner());
        booking.setBooker(booker);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, boolean approved, Long userId) {
        log.info("Approving booking with ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found for ID: {}", bookingId);
                    return new NotFoundException("Booking not found");
                });

        User owner = booking.getOwner();
        User booker = booking.getBooker();
        log.info("Owner: {} (ID: {})", owner.getName(), owner.getId());

        if (!owner.getId().equals(booking.getItem().getOwner().getId())) {
            log.error("User {} attempted to approve a booking they do not own.", owner.getName());
            throw new InvalidParamException("Only the owner can approve the booking", owner.getName());
        }

        if (!owner.getId().equals(userId)) {
            log.error("User ID {} is not the owner of the booking.", userId);
            throw new InvalidParamException("You are not the owner of this booking", userId.toString());
        }

        booking.setAvailable(false);
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking with ID: {} has been {}", bookingId, approved ? "approved" : "rejected");
        log.info("Owner: {} (ID: {}), Booker: {} (ID: {}) approved the booking.", owner.getName(), owner.getId(), booker.getName(), booker.getId());

        // Обновляем доступность предмета
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        item.setAvailable(booking.getAvailable());
        itemRepository.save(item);

        return BookingMapper.toBookingDto(updatedBooking);
    }


    @Override
    public List<BookingDto> getBookingsForCurrentUser(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Status status;
        try {
            status = Status.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidParamException("Invalid state value", state);
        }

        List<Booking> bookings;
        switch (status) {
            case CANCELED -> bookings = bookingRepository.findCancelledForCurrentUser(userId);
            case APPROVED -> bookings = bookingRepository.findApprovedForCurrentUser(userId);
            case WAITING -> bookings = bookingRepository.findWaitingForCurrentUser(userId);
            case REJECTED -> bookings = bookingRepository.findRejectedForCurrentUser(userId);
            case ALL -> bookings = bookingRepository.findAllForCurrentUser(userId);
            case CURRENT -> bookings = bookingRepository.findCurrentForCurrentUser(userId);
            case PAST -> bookings = bookingRepository.findPastForCurrentUser(userId);
            case FUTURE -> bookings = bookingRepository.findFutureForCurrentUser(userId);
            default -> throw new InvalidParamException("Unsupported state:", state);
        }

        return BookingMapper.toBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> getBookingsForOwner(Long ownerId, String state) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Status status;
        try {
            status = Status.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidParamException("Invalid state value", state);
        }

        List<Booking> bookings;
        switch (status) {
            case CANCELED -> bookings = bookingRepository.findCancelledForOwnerId(ownerId);
            case APPROVED -> bookings = bookingRepository.findApprovedForOwnerId(ownerId);
            case WAITING -> bookings = bookingRepository.findWaitingForOwnerId(ownerId);
            case REJECTED -> bookings = bookingRepository.findRejectedForOwnerId(ownerId);
            case ALL -> bookings = bookingRepository.findAllForOwnerId(ownerId);
            case CURRENT -> bookings = bookingRepository.findCurrentForOwnerId(ownerId);
            case PAST -> bookings = bookingRepository.findPastForOwnerId(ownerId);
            case FUTURE -> bookings = bookingRepository.findFutureForOwnerId(ownerId);
            default -> throw new InvalidParamException("Unsupported state:", state);
        }

        return BookingMapper.toBookingDtoList(bookings);
    }
}
