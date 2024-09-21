package ru.practicum.shareit.user.query;

public class UserSql {

    public static final String SQL_USERS_SELECT_ALL = "SELECT * FROM users";

    public static final String SQL_USER_SELECT_BY_ID = "SELECT * FROM users WHERE id = :id";

    public static final String SQL_USER_ADD = "INSERT INTO users (name, email) VALUES (:name, :email)";

    public static final String SQL_USER_UPDATE = "UPDATE users SET name = :name, email = :email WHERE id = :id";

    public static final String SQL_USER_DELETE = "DELETE FROM users WHERE id = :id";

    public static final String SQL_USER_EXISTS_BY_ID = "SELECT EXISTS(SELECT * FROM users WHERE id = :id)";

    public static final String SQL_USER_EXISTS_BY_EMAIL = "SELECT EXISTS(SELECT * FROM users WHERE email = :email)";
}
