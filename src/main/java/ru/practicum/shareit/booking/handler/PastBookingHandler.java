package ru.practicum.shareit.booking.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;

import java.util.List;

@Component
public class PastBookingHandler extends BasicBookingHandler {

    @Autowired
    public PastBookingHandler(@Lazy RejectedBookingHandler next, BookingRepository bookingRepository) {
        super(Status.PAST, next, bookingRepository);
    }

    @Override
    protected List<Booking> handleBy(long personId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findPastForOwnerId(personId);
        } else {
            return bookingRepository.findPastForCurrentUser(personId);
        }
    }
}
