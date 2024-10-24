package ru.practicum.server.exception;
import lombok.Getter;

@Getter
public class InvalidParamException extends RuntimeException {

    private final String parameter;
    private final String reason;

    public InvalidParamException(String parameter, String reason) {
        this.parameter = parameter;
        this.reason = reason;
    }

}
