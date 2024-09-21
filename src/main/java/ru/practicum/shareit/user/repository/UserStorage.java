package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User getUser(Long userId);

    User create(User user);

    User update(Long userId, User user);

    Boolean delete(Long userId);

    Boolean isUserExistById(Long userId);

    Boolean isUserExistByEmail(String email);
}