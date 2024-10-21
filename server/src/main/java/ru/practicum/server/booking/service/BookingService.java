package ru.practicum.server.booking.service;

import ru.practicum.server.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, Long userId);

    BookingDto approveBooking(Long bookingId, boolean approved, Long userId);

    BookingDto getBookingById(Long bookingId);

    List<BookingDto> getBookingsForCurrentUser(Long userId, String state);

    List<BookingDto> getBookingsForOwner(Long userId, String state);
}
