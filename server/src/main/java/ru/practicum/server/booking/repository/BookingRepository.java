package ru.practicum.server.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT * FROM Bookings b WHERE b.booker_id = :userId AND b.item_id = :itemId " +
            "AND b.end_date < :instant " +
            "ORDER BY b.end_date DESC LIMIT 1", nativeQuery = true)
    Optional<Booking> findByItemIdAndUserIdAndEndBookingBefore(Long itemId, Long userId, LocalDateTime instant);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND (:status IS NULL OR b.status = :status) ORDER BY b.start DESC")
    List<Booking> findByBookerAndStatus(long userId, Status status);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND (:status IS NULL OR b.status = :status) ORDER BY b.start DESC")
    List<Booking> findByOwnerAndStatus(long ownerId, Status status);

    // USER

    default List<Booking> findWaitingForCurrentUser(long id) {
        return findByBookerAndStatus(id, Status.WAITING);
    }

    default List<Booking> findCancelledForCurrentUser(long id) {
        return findByBookerAndStatus(id, Status.CANCELED);
    }

    default List<Booking> findApprovedForCurrentUser(long id) {
        return findByBookerAndStatus(id, Status.APPROVED);
    }

    default List<Booking> findRejectedForCurrentUser(long id) {
        return findByBookerAndStatus(id, Status.REJECTED);
    }

    default List<Booking> findAllForCurrentUser(long id) {
        return findByBookerAndStatus(id, null);
    }

    default List<Booking> findCurrentForCurrentUser(long id) {
        return findByBookerAndStatus(id, Status.CURRENT);
    }

    default List<Booking> findPastForCurrentUser(long id) {
        return findByBookerAndStatus(id, Status.PAST);
    }

    default List<Booking> findFutureForCurrentUser(long id) {
        return findByBookerAndStatus(id, Status.FUTURE);
    }

    // OWNER

    default List<Booking> findWaitingForOwnerId(long id) {
        return findByOwnerAndStatus(id, Status.WAITING);
    }

    default List<Booking> findRejectedForOwnerId(long id) {
        return findByOwnerAndStatus(id, Status.REJECTED);
    }

    default List<Booking> findApprovedForOwnerId(long id) {
        return findByOwnerAndStatus(id, Status.APPROVED);
    }

    default List<Booking> findCancelledForOwnerId(long id) {
        return findByOwnerAndStatus(id, Status.CANCELED);
    }

    default List<Booking> findAllForOwnerId(long id) {
        return findByOwnerAndStatus(id, null);
    }

    default List<Booking> findCurrentForOwnerId(long id) {
        return findByOwnerAndStatus(id, Status.CURRENT);
    }

    default List<Booking> findPastForOwnerId(long id) {
        return findByOwnerAndStatus(id, Status.PAST);
    }

    default List<Booking> findFutureForOwnerId(long id) {
        return findByOwnerAndStatus(id, Status.FUTURE);
    }
}
