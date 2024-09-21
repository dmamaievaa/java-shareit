package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getUser(Long userId);

    User create(User user);

    User update(Long userId, User user);

    Boolean delete(Long userId);
}
