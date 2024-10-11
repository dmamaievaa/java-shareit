package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner() != null ? item.getOwner().getId() : null)
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(item.getLastBooking() != null ? item.getLastBooking().toString() : null)
                .nextBooking(item.getNextBooking() != null ? item.getNextBooking().toString() : null)
                .comments(toCommentDtoList(item.getComments()))
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        if (itemDto == null) {
            return null;
        }

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }
        return comments.stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null)
                        .created(comment.getCreated())
                        .build())
                .collect(Collectors.toList());
    }

    public static Item itemPatch(Item currentItem, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            currentItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            currentItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            currentItem.setAvailable(itemDto.getAvailable());
        }
        return currentItem;
    }
}