package ru.practicum.server.booking.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.Status;

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
