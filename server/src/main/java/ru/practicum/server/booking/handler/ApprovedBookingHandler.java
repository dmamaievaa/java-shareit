package ru.practicum.server.booking.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.Status;

import java.util.List;

@Component
public class ApprovedBookingHandler extends BasicBookingHandler {

    @Autowired
    public ApprovedBookingHandler(@Lazy CancelledBookingHandler next, BookingRepository bookingRepository) {
        super(Status.APPROVED, next, bookingRepository);
    }

    @Override
    protected List<Booking> handleBy(long personId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findApprovedForOwnerId(personId);
        } else {
            return bookingRepository.findApprovedForCurrentUser(personId);
        }
    }
}
