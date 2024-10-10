package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT * FROM Bookings b WHERE b.booker_id = :userId AND b.item_id = :itemId " +
            "AND b.end_date < :instant " +
            "ORDER BY b.end_date DESC LIMIT 1", nativeQuery = true)
    Optional<Booking> findByItemIdAndUserIdAndEndBookingBefore(Long itemId, Long userId, LocalDateTime instant);

    // USER

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :id AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> findWaitingForCurrentUser(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :id AND b.status = 'CANCELED' ORDER BY b.start DESC")
    List<Booking> findCancelledForCurrentUser(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :id AND b.status = 'APPROVED' ORDER BY b.start DESC")
    List<Booking> findApprovedForCurrentUser(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :id AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> findRejectedForCurrentUser(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :id ORDER BY b.start DESC")
    List<Booking> findAllForCurrentUser(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :id AND b.status = 'APPROVED' " +
            "AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findCurrentForCurrentUser(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :id AND b.status = 'APPROVED' " +
            "AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findPastForCurrentUser(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :id AND b.status = 'APPROVED' " +
            "AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findFutureForCurrentUser(long id);

    // OWNER

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :id AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> findWaitingForOwnerId(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :id AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> findRejectedForOwnerId(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :id AND b.status = 'APPROVED' ORDER BY b.start DESC")
    List<Booking> findApprovedForOwnerId(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :id AND b.status = 'CANCELED' ORDER BY b.start DESC")
    List<Booking> findCancelledForOwnerId(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :id ORDER BY b.start DESC")
    List<Booking> findAllForOwnerId(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :id AND b.status = 'APPROVED' " +
            "AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findCurrentForOwnerId(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :id AND b.status = 'APPROVED' " +
            "AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findPastForOwnerId(long id);

    @EntityGraph(attributePaths = {"item", "booker", "owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :id AND b.status = 'APPROVED' " +
            "AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findFutureForOwnerId(long id);
}

