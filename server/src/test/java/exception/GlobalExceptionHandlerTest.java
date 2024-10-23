package exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.server.exception.ErrorResponse;
import ru.practicum.server.exception.ForbiddenException;
import ru.practicum.server.exception.GlobalExceptionHandler;
import ru.practicum.server.exception.InvalidItemException;
import ru.practicum.server.exception.InvalidParamException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.UserAlreadyExists;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFoundException_shouldReturnNotFoundResponse() {
        NotFoundException exception = new NotFoundException("Item not found");

        ErrorResponse response = globalExceptionHandler.handleNotFoundException(exception);

        assertEquals("Not Found", response.getError());
        assertEquals("Item not found", response.getMessage());
    }

    @Test
    void handleInvalidItemException_shouldReturnBadRequestResponse() {
        InvalidItemException exception = new InvalidItemException("Invalid item data");

        ErrorResponse response = globalExceptionHandler.handleInvalidItemException(exception);

        assertEquals("Bad Request", response.getError());
        assertEquals("Invalid item data", response.getMessage());
    }

    @Test
    void handleUserAlreadyExists_shouldReturnConflictResponse() {
        UserAlreadyExists exception = new UserAlreadyExists("User already exists");

        ErrorResponse response = globalExceptionHandler.handleUserAlreadyExists(exception);

        assertEquals("Conflict", response.getError());
        assertEquals("User already exists", response.getMessage());
    }

    @Test
    void handleForbiddenException_shouldReturnForbiddenResponse() {
        ForbiddenException exception = new ForbiddenException("Access is forbidden");

        ErrorResponse response = globalExceptionHandler.handleForbiddenException(exception);

        assertEquals("Forbidden", response.getError());
        assertEquals("Access is forbidden", response.getMessage());
    }

    @Test
    void handleInvalidParamException_shouldReturnBadRequestResponse() {

        InvalidParamException exception = new InvalidParamException("param1", "Invalid format");

        ErrorResponse response = globalExceptionHandler.handleInvalidParamException(exception);

        assertEquals("Bad Request", response.getError());
    }

    @Test
    void handleGenericException_shouldReturnInternalServerErrorResponse() {
        Exception exception = new Exception("Unexpected error occurred");

        ErrorResponse response = globalExceptionHandler.handleGenericException(exception);

        assertEquals("Internal Server Error", response.getError());
        assertEquals("An unexpected error occurred: Unexpected error occurred", response.getMessage());
    }
}