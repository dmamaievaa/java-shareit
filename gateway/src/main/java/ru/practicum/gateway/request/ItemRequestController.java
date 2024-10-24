package ru.practicum.gateway.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.gateway.request.dto.ItemRequestDto;
import ru.practicum.gateway.utils.GlobalConstants;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader(GlobalConstants.USERID_HEADER) Long userId) {
        log.info("Started creating new request by user with id = {}", userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(GlobalConstants.USERID_HEADER) Long userId) {
        log.info("Started getting all request with item by user with id = {}", userId);
        return itemRequestClient.getAllByUser(userId);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable(name = "requestId") Long requestId) {
        log.info("Started getting all request with item by id = {}", requestId);
        return itemRequestClient.getById(requestId);
    }
}
