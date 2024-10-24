package ru.practicum.server.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.server.exception.DataNotFoundException;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.mapper.ItemRequestMapper;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.server.utils.GlobalConstants.REQUEST_NOT_FOUND;
import static ru.practicum.server.utils.GlobalConstants.USER_NOT_FOUND;


@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = findUserById(userId);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllByUser(Long userId) {
        User user = findUserById(userId);
        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(user.getId())
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        requests.forEach(this::setItems);

        return requests;
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format(USER_NOT_FOUND, userId)));

        List<ItemRequestDto> itemsRequest = itemRequestRepository.findAll(Sort.by("created").descending())
                .stream()
                .filter(request -> !request.getRequestor().getId().equals(userId))
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        itemsRequest.forEach(this::setItems);

        return itemsRequest;
    }

    @Override
    public ItemRequestDto getById(Long requestId) {
        log.info("Received request to retrieve item request by id: {}", requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException(String.format(REQUEST_NOT_FOUND, requestId)));

        log.info("Item request retrieved: {}", itemRequest);
        ItemRequestDto itemRequestResponseDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        setItems(itemRequestResponseDto);

        return itemRequestResponseDto;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format(USER_NOT_FOUND, userId)));
    }

    private void setItems(ItemRequestDto itemRequestResponseDto) {
        log.info("Setting items for request ID: {}", itemRequestResponseDto.getId());
        List<Item> items = itemRepository.findAllByRequestId(itemRequestResponseDto.getId());

        log.info("Found {} items for request ID: {}", items.size(), itemRequestResponseDto.getId());
        itemRequestResponseDto.setItems(items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }
}