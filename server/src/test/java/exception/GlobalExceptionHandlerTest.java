package exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.server.exception.ErrorResponse;
import ru.practicum.server.exception.ForbiddenException;
import ru.practicum.server.exception.GlobalExceptionHandler;
import ru.practicum.server.exception.InvalidItemException;
import ru.practicum.server.exception.InvalidParamException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.UserAlreadyExists;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationExceptions_shouldReturnBadRequestResponse() {

        String fieldName = "email";
        String errorMessage = "must be a valid email";
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("user", fieldName, errorMessage));

        BindingResult bindingResult = mock(BindingResult.class);
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                null,
                bindingResult
        );

        ErrorResponse response = globalExceptionHandler.handleValidationExceptions(exception);

        assertEquals("Validation Failed", response.getError());
        assertEquals("email: must be a valid email; ", response.getMessage());
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