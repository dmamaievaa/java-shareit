package ru.practicum.shareit.booking.handler;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;

import java.util.List;

public class WaitingBookingHandler extends BasicBookingHandler {
    private final BookingRepository bookingRepository;

    public WaitingBookingHandler(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> handle(Long personId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findWaitingForOwnerId(personId);
        }
        return bookingRepository.findWaitingForCurrentUser(personId);
    }

    @Override
    public boolean canHandle(Status status) {
        return status == Status.WAITING;
    }

    @Override
    public BookingHandler getNext() {
        return next;
    }
}