package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(message = "Item name cannot be blank")
    private String name;

    @NotBlank(message = "Item description cannot be blank")
    private String description;

    @NotNull(message = "Item availability status cannot be null")
    private Boolean available;

    private Long owner;

    private Long request;

    private String lastBooking;

    private String nextBooking;

    private List<CommentDto> comments;
}