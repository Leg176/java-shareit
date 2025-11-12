package ru.practicum.shareit.error;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exceptions.BadRequestException;
import ru.practicum.shareit.error.exceptions.ForbiddenException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void handleValidation_ShouldReturnConflictStatus() {
        String errorMessage = "Validation failed";
        ValidationException exception = new ValidationException(errorMessage);

        ErrorResponse response = errorHandler.handleValidation(exception);

        assertThat(response.getError()).isEqualTo(errorMessage);
    }

    @Test
    void handleNotFound_ShouldReturnNotFoundStatus() {
        String errorMessage = "Resource not found";
        NotFoundException exception = new NotFoundException(errorMessage);

        ErrorResponse response = errorHandler.handleNotFound(exception);

        assertThat(response.getError()).isEqualTo(errorMessage);
    }

    @Test
    void handleBadRequest_ShouldReturnBadRequestStatus() {
        String errorMessage = "Bad request";
        BadRequestException exception = new BadRequestException(errorMessage);

        ErrorResponse response = errorHandler.handleBadRequest(exception);

        assertThat(response.getError()).isEqualTo(errorMessage);
    }

    @Test
    void handleForbidden_ShouldReturnForbiddenStatus() {
        String errorMessage = "Access forbidden";
        ForbiddenException exception = new ForbiddenException(errorMessage);

        ErrorResponse response = errorHandler.handleForbidden(exception);

        assertThat(response.getError()).isEqualTo(errorMessage);
    }

    @Test
    void handleConstraintViolation_ShouldReturnBadRequestStatus() {
        String errorMessage = "Constraint violation";
        ConstraintViolationException exception = new ConstraintViolationException(errorMessage, null);

        ErrorResponse response = errorHandler.handleConstraintViolation(exception);

        assertThat(response.getError()).isEqualTo(errorMessage);
    }

    @Test
    void handleValidation_WithDifferentMessages_ShouldReturnCorrectMessage() {
        String errorMessage = "Another validation error";
        ValidationException exception = new ValidationException(errorMessage);

        ErrorResponse response = errorHandler.handleValidation(exception);

        assertThat(response.getError()).isEqualTo(errorMessage);
    }

    @Test
    void handleNotFound_WithEmptyMessage_ShouldReturnEmptyMessage() {
        String errorMessage = "";
        NotFoundException exception = new NotFoundException(errorMessage);

        ErrorResponse response = errorHandler.handleNotFound(exception);

        assertThat(response.getError()).isEqualTo("");
    }

    @Test
    void handleBadRequest_WithNullMessage_ShouldReturnNullMessage() {
        BadRequestException exception = new BadRequestException(null);

        ErrorResponse response = errorHandler.handleBadRequest(exception);

        assertThat(response.getError()).isNull();
    }
}
