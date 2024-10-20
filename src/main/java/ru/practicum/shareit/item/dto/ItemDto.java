package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    Long id;

    @NotBlank(message = "Item name cannot be blank")
    String name;

    @NotBlank(message = "Item description cannot be blank")
    String description;

    @NotNull(message = "Item availability status cannot be null")
    Boolean available;

    Long owner;

    Long request;

    String lastBooking;

    String nextBooking;

    List<CommentDto> comments;

    Long requestId;
}