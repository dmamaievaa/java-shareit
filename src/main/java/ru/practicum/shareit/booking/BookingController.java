package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import jakarta.validation.Valid;
import ru.practicum.shareit.utils.GlobalConstants;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(
            @RequestHeader(GlobalConstants.USERID_HEADER) Long userId,
            @RequestBody @Valid BookingDto bookingDto) {
        log.info("Starting booking creation for user ID: {} with booking details: {}", userId, bookingDto);
        BookingDto createdBooking = bookingService.createBooking(bookingDto, userId);
        log.info("Booking successfully created with ID: {}", createdBooking.getId());
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader(GlobalConstants.USERID_HEADER) Long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        log.info("User ID: {} is attempting to approve booking ID: {} with approval status: {}", userId, bookingId, approved);
        BookingDto updatedBooking = bookingService.approveBooking(bookingId, approved, userId);
        log.info("Booking ID: {} approval status updated to: {}", bookingId, approved);
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId,
                                 @PathVariable Long bookingId) {
        log.info("Fetching booking details for booking ID: {} by user ID: {}", bookingId, userId);
        BookingDto booking = bookingService.getBookingById(bookingId);
        log.info("Fetched booking details: {}", booking);
        return booking;
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Fetching bookings for user ID: {} with state: {}", userId, state);
        List<BookingDto> userBookings = bookingService.getBookingsForCurrentUser(userId, state);
        log.info("Fetched {} bookings for user ID: {}", userBookings.size(), userId);
        return userBookings;
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Fetching owner bookings for user ID: {} with state: {}", userId, state);
        List<BookingDto> ownerBookings = bookingService.getBookingsForOwner(userId, state);
        log.info("Fetched {} owner bookings for user ID: {}", ownerBookings.size(), userId);
        return ownerBookings;
    }
}
