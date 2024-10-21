package ru.practicum.server.booking.handler;

import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.enums.Status;

import java.util.List;

public interface BookingHandler {

    List<Booking> handle(Long personId, boolean isOwner, Status status);
}

