package ru.practicum.server.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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