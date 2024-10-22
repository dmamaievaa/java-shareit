package ru.practicum.server.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.server.enums.Status;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.user.dto.UserDto;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;

    @NotNull(message = "Start date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")  // Добавлены секунды
    LocalDateTime start;

    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")  // Добавлены секунды
    LocalDateTime end;

    @NotNull(message = "Item ID cannot be null")
    Long itemId;

    Long bookerId;

    Status status;

    Boolean approved;

    Long ownerId;

    ItemDto item;

    UserDto booker;
}
