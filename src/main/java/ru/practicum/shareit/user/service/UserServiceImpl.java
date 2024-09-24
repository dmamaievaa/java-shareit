package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExists;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserStorage;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userRepository;

    public List<UserDto> getAll() {
        List<User> users = userRepository.getAll();
        return UserMapper.toUserDtoList(users);
    }

    public UserDto getUser(Long userId) {
        if (!userRepository.isUserExistById(userId)) {
            throw new NotFoundException("User not found");
        }
        User user = userRepository.getUser(userId);
        return UserMapper.toUserDto(user);
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Incorrect email.");
        }
        if (userRepository.isUserExistByEmail(user.getEmail())) {
            throw new UserAlreadyExists("User already exists");
        }
        User createdUser = userRepository.create(user);
        return UserMapper.toUserDto(createdUser);
    }

    public UserDto update(Long userId, UserDto userDto) {
        if (!userRepository.isUserExistById(userId)) {
            throw new NotFoundException("User not found");
        }
        User user = UserMapper.toUser(userDto);
        User updatedUser = userRepository.update(userId, user);
        return UserMapper.toUserDto(updatedUser);
    }

    public Boolean delete(Long userId) {
        if (!userRepository.isUserExistById(userId)) {
            throw new NotFoundException("User not found");
        }
        return userRepository.delete(userId);
    }
}