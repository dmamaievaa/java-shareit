package ru.practicum.server.booking.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.enums.Status;

import java.util.List;

@Component
public class RejectedBookingHandler extends BasicBookingHandler {

    @Autowired
    public RejectedBookingHandler(@Lazy WaitingBookingHandler next, BookingRepository bookingRepository) {
        super(Status.REJECTED, next, bookingRepository);
    }

    @Override
    protected List<Booking> handleBy(long personId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findRejectedForOwnerId(personId);
        } else {
            return bookingRepository.findRejectedForCurrentUser(personId);
        }
    }
}
