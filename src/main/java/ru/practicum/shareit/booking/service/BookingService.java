package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto);

    BookingDto approveBooking(Long bookingId, boolean approved, Long userId);

    BookingDto getBookingById(Long bookingId);

    List<BookingDto> getBookingsForCurrentUser(Long userId, String state);

    List<BookingDto> getBookingsForOwner(Long userId, String state);
}
