package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidParamException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.utils.GlobalConstants.ITEM_NOT_FOUND;
import static ru.practicum.shareit.utils.GlobalConstants.USER_NOT_BOOKED_ITEM;
import static ru.practicum.shareit.utils.GlobalConstants.USER_NOT_FOUND;


import java.time.Instant;
import java.util.List;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;


    public List<ItemDto> findAll(Long userId) {
        List<Item> items = itemRepository.getAllByUserId(userId);
        return ItemMapper.toItemDtoList(items);
    }

    public ItemDto get(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND));
        return ItemMapper.toItemDto(item);
    }

    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Item item = ItemMapper.toItem(itemDto, owner);

        Item createdItem = itemRepository.save(item);

        return ItemMapper.toItemDto(createdItem);
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException(USER_NOT_FOUND);
        }

        ItemMapper.itemPatch(existingItem, itemDto);

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    public Boolean delete(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(ITEM_NOT_FOUND);
        }
        itemRepository.deleteById(itemId);
        return true;
    }

    public List<ItemDto> search(String text) {
        List<Item> items = itemRepository.search(text);
        return ItemMapper.toItemDtoList(items);
    }

    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND));

        bookingRepository.findByItemIdAndUserIdAndEndBookingBefore(itemId, userId, LocalDateTime.now())
                .orElseThrow(() -> new InvalidParamException(USER_NOT_BOOKED_ITEM, userId.toString()));

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(Instant.now());

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }

    public List<CommentDto> getComments(Long itemId) {
        List<Comment> comments = commentRepository.findByItem_Id(itemId);
        return CommentMapper.toCommentDtoList(comments);
    }
}
