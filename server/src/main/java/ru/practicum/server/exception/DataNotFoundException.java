package ru.practicum.server.exception;

public class DataNotFoundException extends RuntimeException {
    private final String parameter;

    public DataNotFoundException(String parameter) {
        super("Data not found for parameter: " + parameter);
        this.parameter = parameter;
    }
}