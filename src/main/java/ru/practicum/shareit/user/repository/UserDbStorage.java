package ru.practicum.shareit.user.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.UserAlreadyExists;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.query.UserSql;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<User> getAll() {
        return jdbc.query(UserSql.SQL_USERS_SELECT_ALL, this::mapRow);
    }

    @Override
    public User getUser(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        return jdbc.queryForObject(UserSql.SQL_USER_SELECT_BY_ID, params, this::mapRow);
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("email", user.getEmail());

        jdbc.update(UserSql.SQL_USER_ADD, params, keyHolder, new String[]{"id"});

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User updateUser = getUser(userId);

        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }

        if (user.getEmail() != null && !updateUser.getEmail().equals(user.getEmail())) {
            if (!isUserExistByEmail(user.getEmail())) {
                updateUser.setEmail(user.getEmail());
            } else {
                throw new UserAlreadyExists("Email is already in use");
            }
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", updateUser.getName())
                .addValue("email", updateUser.getEmail())
                .addValue("id", updateUser.getId());

        jdbc.update(UserSql.SQL_USER_UPDATE, params);
        return updateUser;
    }

    @Override
    public Boolean delete(Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", userId);

        return jdbc.update(UserSql.SQL_USER_DELETE, params) > 0;
    }

    @Override
    public Boolean isUserExistById(Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", userId);

        return Boolean.TRUE.equals(jdbc.queryForObject(UserSql.SQL_USER_EXISTS_BY_ID, params, Boolean.class));
    }

    @Override
    public Boolean isUserExistByEmail(String email) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email);

        return Boolean.TRUE.equals(jdbc.queryForObject(UserSql.SQL_USER_EXISTS_BY_EMAIL, params, Boolean.class));
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .build();
    }
}
