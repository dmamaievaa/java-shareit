/*package ru.practicum.shareit.item.query;

public class ItemSql {

    public static final String SQL_ITEMS_SELECT_BY_USER_ID = "SELECT * FROM item WHERE owner = :userId";

    public static final String SQL_ITEM_SELECT_BY_ID = "SELECT * FROM item WHERE id = :itemId";

    public static final String SQL_ITEM_ADD = "INSERT INTO item (name, description, available, owner) " +
            "VALUES (:name, :description, :available, :owner)";

    public static final String SQL_ITEM_UPDATE = "UPDATE item SET name = :name, description = :description, available = :available " +
            "WHERE id = :itemId AND owner = :ownerId";

    public static final String SQL_ITEM_DELETE = "DELETE FROM item WHERE id = :itemId";

    public static final String SQL_ITEM_EXISTS = "SELECT EXISTS(SELECT * FROM item WHERE id = :itemId)";

    public static final String SQL_ITEM_SEARCH = "SELECT * FROM item WHERE " +
            "(LOWER(name) LIKE :search OR LOWER(description) LIKE :search) AND available = true";
}
*/