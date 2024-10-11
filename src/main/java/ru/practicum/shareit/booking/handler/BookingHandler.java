package ru.practicum.shareit.booking.handler;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;

import java.util.List;

public interface BookingHandler {

    List<Booking> handle(Long personId, boolean isOwner);

    void setNext(BookingHandler next);

    boolean canHandle(Status status);

    BookingHandler getNext();
}

