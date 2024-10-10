package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private Long id;

    @NotNull(message = "Start date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime start;

    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime end;

    @NotNull(message = "Item ID cannot be null")
    private Long itemId;

    private Long bookerId;

    private Status status;

    private Boolean approved;

    private Long ownerId;

    private ItemDto item;
    private UserDto booker;

    @AssertTrue(message = "Start date must be before end date")
    public boolean isStartBeforeEnd() {
        return start.isBefore(end);
   }
}
