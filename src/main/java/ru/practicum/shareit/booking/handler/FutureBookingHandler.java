package ru.practicum.shareit.booking.handler;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;

import java.util.List;

public class FutureBookingHandler extends BasicBookingHandler {
    private final BookingRepository bookingRepository;

    public FutureBookingHandler(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> handle(Long personId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findFutureForOwnerId(personId);
        }
        return bookingRepository.findFutureForCurrentUser(personId);
    }

    @Override
    public boolean canHandle(Status status) {
        return status == Status.FUTURE;
    }

    @Override
    public BookingHandler getNext() {
        return next;
    }
}