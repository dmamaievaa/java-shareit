package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExists;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userRepository;


    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User getUser(Long userId) {
        if (!userRepository.isUserExistById(userId)) {
            throw new NotFoundException("User not found");
        }
        return userRepository.getUser(userId);
    }

    public User create(User user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Incorrect email.");
        }
        if (userRepository.isUserExistByEmail(user.getEmail())) {
            throw new UserAlreadyExists("User already exists");
        }
        return userRepository.create(user);
    }

    public User update(Long userId, User user) {
        if (!userRepository.isUserExistById(userId)) {
            throw new NotFoundException("User not found");
        }
        return userRepository.update(userId, user);
    }

    public Boolean delete(Long userId) {
        if (!userRepository.isUserExistById(userId)) {
            throw new NotFoundException("User not found");
        }
        return userRepository.delete(userId);
    }
}