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

import static ru.practicum.shareit.utils.GlobalConstants.EMAIL_ALREADY_IN_USE;
import static ru.practicum.shareit.utils.GlobalConstants.INCORRECT_EMAIL;
import static ru.practicum.shareit.utils.GlobalConstants.USER_ALREADY_EXISTS;
import static ru.practicum.shareit.utils.GlobalConstants.USER_NOT_FOUND;

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
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        return UserMapper.toUserDto(user);
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validateEmail(user.getEmail());
        User createdUser = userRepository.save(user);
        return UserMapper.toUserDto(createdUser);
    }

    public UserDto update(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userRepository.existsByEmail(userDto.getEmail()) && !existingUser.getEmail().equals(userDto.getEmail())) {
                throw new UserAlreadyExists(EMAIL_ALREADY_IN_USE);
            }
            existingUser.setEmail(userDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toUserDto(updatedUser);
    }

    public Boolean delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
        return true;
    }

    private void validateEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException(INCORRECT_EMAIL);
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExists(USER_ALREADY_EXISTS);
        }
    }
}
