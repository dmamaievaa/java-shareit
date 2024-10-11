package ru.practicum.shareit.booking.handler;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;

import java.util.List;

public abstract class BasicBookingHandler implements BookingHandler {
    protected BookingHandler next;

    @Override
    public void setNext(BookingHandler next) {
        this.next = next;
    }

    @Override
    public List<Booking> handle(Long personId, boolean isOwner) {
        if (next != null) {
            return next.handle(personId, isOwner);
        }
        return null;
    }

    public abstract boolean canHandle(Status status);
}