package ru.practicum.shareit.booking.handler;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;

import java.util.List;

public abstract class BasicBookingHandler implements BookingHandler {
    protected final Status status;
    protected final BookingHandler next;
    protected final BookingRepository bookingRepository;

    public BasicBookingHandler(Status status, BookingHandler next,  @Autowired BookingRepository bookingRepository) {
        this.status = status;
        this.next = next;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> handle(Long personId, boolean isOwner, Status status) {
        if (this.status == status) {
            return handleBy(personId, isOwner);
        } else if (next != null) {
            return next.handle(personId, isOwner, status);
        }
        return null;
    }

    protected abstract List<Booking> handleBy(long personId, boolean isOwner);
}
