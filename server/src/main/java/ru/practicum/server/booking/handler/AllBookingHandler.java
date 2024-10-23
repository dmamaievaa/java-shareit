package ru.practicum.server.booking.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.Status;

import java.util.List;

@Component
@Primary
public class AllBookingHandler extends BasicBookingHandler {

    @Autowired
    public AllBookingHandler(@Lazy ApprovedBookingHandler next, BookingRepository bookingRepository) {
        super(Status.ALL, next, bookingRepository);
    }

    @Override
    protected List<Booking> handleBy(long personId, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findAllForOwnerId(personId);
        } else {
            return bookingRepository.findAllForCurrentUser(personId);
        }
    }
}
