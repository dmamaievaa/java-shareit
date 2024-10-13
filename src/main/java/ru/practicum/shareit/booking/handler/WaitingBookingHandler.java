package ru.practicum.shareit.booking.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;

import java.util.List;

@Component
public class WaitingBookingHandler extends BasicBookingHandler {

    @Autowired
    public WaitingBookingHandler(BookingHandler next, BookingRepository bookingRepository) {
        super(Status.WAITING, next, bookingRepository);
    }

    @Override
    protected List<Booking> handleBy(long personId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findWaitingForOwnerId(personId);
        } else {
            return bookingRepository.findWaitingForCurrentUser(personId);
        }
    }
}
