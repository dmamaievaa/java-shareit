package ru.practicum.gateway.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.gateway.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Started creating new user");
        return userClient.create(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Started getting all users");
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable(value = "userId") Long id) {
        log.info("Started getting user by id = {}", id);
        return userClient.get(id);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UserDto userDto,
                                         @PathVariable(value = "userId") Long id) {
        log.info("Started updating user by id = {}", id);
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable(value = "userId") Long id) {
        log.info("Started deleting user by id = {}", id);
        return userClient.delete(id);
    }
}