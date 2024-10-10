package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;

    @NotNull(message = "Start date cannot be null")
    LocalDateTime start;

    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in the future")
    LocalDateTime end;

    @NotNull(message = "Item ID cannot be null")
    Long itemId;

    Long bookerId;

    Status status;

    Boolean approved;

    Long ownerId;

    ItemDto item;

    UserDto booker;

    @AssertTrue(message = "Start date must be before end date")
    public boolean isStartBeforeEnd() {
        return start.isBefore(end);
   }
}
