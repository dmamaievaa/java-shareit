package ru.practicum.shareit.user.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserAlreadyExists;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Repository
public class InMemoryUserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();
    private Long id = 0L;


    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User currentUser = getUser(userId);
        if (currentUser == null) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        String oldEmail = currentUser.getEmail();
        String newEmail = user.getEmail();

        if (user.getName() != null) {
            currentUser.setName(user.getName());
        }

        if (newEmail != null && !oldEmail.equals(newEmail)) {
            if (isUserExistByEmail(newEmail)) {
                throw new UserAlreadyExists("Email is already in use : " + newEmail);
            }
            currentUser.setEmail(newEmail);
            emails.remove(oldEmail);
            emails.put(newEmail, userId);
        }

        users.put(userId, currentUser);
        return currentUser;
    }

    @Override
    public Boolean delete(Long userId) {
        User user = getUser(userId);
        if (user == null) {
            return false;
        }

        emails.remove(user.getEmail());
        users.remove(userId);
        return true;
    }

    @Override
    public Boolean isUserExistById(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public Boolean isUserExistByEmail(String email) {
        return emails.containsKey(email);
    }
}