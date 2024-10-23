package ru.practicum.server.booking.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.server.booking.Status;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NamedEntityGraph(name = "booking.detail", attributeNodes = {
        @NamedAttributeNode("owner"),
        @NamedAttributeNode("item"),
        @NamedAttributeNode("booker")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "start_date", nullable = false)
    @Builder.Default
    LocalDateTime start = LocalDateTime.now();

    @Column(name = "end_date", nullable = false)
    @Builder.Default
    LocalDateTime end = LocalDateTime.now().plusDays(1);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    User booker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    Status status = Status.WAITING;

    @Column(name = "available", nullable = false)
    Boolean available;
}
