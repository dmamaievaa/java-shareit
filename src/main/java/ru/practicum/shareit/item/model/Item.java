package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.apache.catalina.connector.Request;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class Item {

    private Long id;

    @NotBlank(message = "Item name cannot be blank")
    private String name;

    @NotBlank(message = "Item description cannot be blank")
    private String description;

    @NotNull(message = "Item availability status cannot be null")
    private Boolean available;
    private User owner;
    private Request request;
}