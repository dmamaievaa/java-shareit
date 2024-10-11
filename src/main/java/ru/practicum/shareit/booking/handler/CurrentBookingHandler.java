package ru.practicum.shareit.booking.handler;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;

import java.util.List;

public class CurrentBookingHandler extends BasicBookingHandler {
    private final BookingRepository bookingRepository;

    public CurrentBookingHandler(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> handle(Long personId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findCurrentForOwnerId(personId);
        }
        return bookingRepository.findCurrentForCurrentUser(personId);
    }

    @Override
    public boolean canHandle(Status status) {
        return status == Status.CURRENT;
    }

    @Override
    public BookingHandler getNext() {
        return next;
    }
}