package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExists;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDtoList(users);
    }

    public UserDto get(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return UserMapper.toUserDto(user);
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Incorrect email.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExists("User already exists");
        }
        User createdUser = userRepository.save(user);
        return UserMapper.toUserDto(createdUser);
    }

    public UserDto update(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userRepository.existsByEmail(userDto.getEmail()) && !existingUser.getEmail().equals(userDto.getEmail())) {
                throw new UserAlreadyExists("Email is already in use");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toUserDto(updatedUser);
    }

    public Boolean delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(userId);
        return true;
    }
}
