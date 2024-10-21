package ru.practicum.server.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.service.ItemRequestService;
import ru.practicum.server.utils.GlobalConstants;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Received request to create a request {} from user with id {}", itemRequestDto, userId);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId) {
        log.info("Received request to retrieve the list of requests from user with id {}", userId);
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId) {
        log.info("Received request to retrieve the list of requests from user with id {} created by other users",
                userId);
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId,
                                          @RequestHeader(GlobalConstants.USERID_HEADER) Long userId) {
        log.info("Received request to retrieve request by id");
        return itemRequestService.getById(requestId, userId);
    }
}
