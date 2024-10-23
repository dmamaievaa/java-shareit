package ru.practicum.server.booking.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.Status;

import java.util.List;

@Component
public class FutureBookingHandler extends BasicBookingHandler {

    @Autowired
    public FutureBookingHandler(@Lazy PastBookingHandler next, BookingRepository bookingRepository) {
        super(Status.FUTURE, next, bookingRepository);
    }

    @Override
    protected List<Booking> handleBy(long personId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findFutureForOwnerId(personId);
        } else {
            return bookingRepository.findFutureForCurrentUser(personId);
        }
    }
}
