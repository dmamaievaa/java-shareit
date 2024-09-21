package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.item.query.ItemSql;
import ru.practicum.shareit.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemDbStorage implements ItemStorage {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Item> getAllByUserId(Long userId) {
        log.info("Fetching all items for user with ID: {}", userId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        return jdbc.query(ItemSql.SQL_ITEMS_SELECT_BY_USER_ID, params, this::mapRow);
    }

    @Override
    public Item getItem(Long itemId) {
        log.info("Fetching item with ID: {}", itemId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("itemId", itemId);

        return jdbc.queryForObject(ItemSql.SQL_ITEM_SELECT_BY_ID, params, this::mapRow);
    }

    @Override
    public Item create(Long userId, Item item) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }

        ItemValidator.validateItem(item);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", item.getName())
                .addValue("description", item.getDescription())
                .addValue("available", item.getAvailable())
                .addValue("owner", userId);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(ItemSql.SQL_ITEM_ADD, params, keyHolder, new String[]{"id"});

        item.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {

        Item updatedItem = getItem(itemId);

        if (!Objects.equals(updatedItem.getOwner().getId(), userId)) {
            throw new NotFoundException("User with ID " + userId + " doesn't have access to item " + itemId + ".");
        }

        ItemValidator.validateItem(updatedItem);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", updatedItem.getName())
                .addValue("description", updatedItem.getDescription())
                .addValue("available", updatedItem.getAvailable())
                .addValue("itemId", updatedItem.getId())
                .addValue("ownerId", userId);

        jdbc.update(ItemSql.SQL_ITEM_UPDATE, params);
        return updatedItem;
    }

    @Override
    public Boolean delete(Long itemId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("itemId", itemId);

        return jdbc.update(ItemSql.SQL_ITEM_DELETE, params) > 0;
    }

    @Override
    public List<Item> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }

        String search = '%' + text.toLowerCase() + '%';
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("search", search);

        return jdbc.query(ItemSql.SQL_ITEM_SEARCH, params, this::mapRow);
    }

    public Boolean isUserExist(Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        String sqlQuery = "SELECT EXISTS(SELECT * FROM users WHERE id = :userId)";
        return Boolean.TRUE.equals(jdbc.queryForObject(sqlQuery, params, Boolean.class));
    }

    @Override
    public Boolean isItemExist(Long itemId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("itemId", itemId);

        return Boolean.TRUE.equals(jdbc.queryForObject(ItemSql.SQL_ITEM_EXISTS, params, Boolean.class));
    }

    private User mapRowUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .build();
    }

    private Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Item.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .available(rs.getBoolean("available"))
                .owner(User.builder()
                        .id(rs.getLong("owner"))
                        .build())
                .build();
    }
}
