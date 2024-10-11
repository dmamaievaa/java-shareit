package ru.practicum.shareit.booking.handler;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;

import java.util.List;

public class PastBookingHandler extends BasicBookingHandler {
    private final BookingRepository bookingRepository;

    public PastBookingHandler(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> handle(Long personId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findPastForOwnerId(personId);
        }
        return bookingRepository.findPastForCurrentUser(personId);
    }

    @Override
    public boolean canHandle(Status status) {
        return status == Status.PAST;
    }

    @Override
    public BookingHandler getNext() {
        return next;
    }
}

