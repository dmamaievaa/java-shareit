package ru.practicum.server.request.service;

import ru.practicum.server.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllByUser(Long userId);

    List<ItemRequestDto> getAll(Long userId);

    ItemRequestDto getById(Long requestId, Long userId);
}