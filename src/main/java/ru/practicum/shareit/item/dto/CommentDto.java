package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.Instant;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {

    private Long id;

    private String text;

    private Long itemId;

    private Long authorId;

    private String authorName;

    private Instant created;
}
