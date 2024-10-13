package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto get(Long userId);

    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    Boolean delete(Long userId);
}
