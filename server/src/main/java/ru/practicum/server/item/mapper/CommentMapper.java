package ru.practicum.server.item.mapper;

import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .authorId(comment.getAuthor().getId())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public static Comment toComment(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }

        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }


    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
