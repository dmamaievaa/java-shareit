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

import java.time.Instant;
import java.util.List;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String ITEM_NOT_FOUND = "Item not found.";
    private static final String USER_NOT_FOUND = "User not found.";
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

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

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
              .orElseThrow(() -> new InvalidParamException("User didn't book this item", userId.toString()));

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
