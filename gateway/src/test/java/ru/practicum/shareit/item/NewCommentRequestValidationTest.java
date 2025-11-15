package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class NewCommentRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenValidData_thenNoViolations() {
        NewCommentRequest dto = NewCommentRequest.builder()
                .text("Great item!")
                .build();

        Set<ConstraintViolation<NewCommentRequest>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenTextIsBlank_thenValidationFails() {
        NewCommentRequest dto = NewCommentRequest.builder()
                .text("")
                .build();

        Set<ConstraintViolation<NewCommentRequest>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewCommentRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("text");
        assertThat(violation.getMessage()).isEqualTo("Комментарий не может быть пустым.");
    }

    @Test
    void whenTextIsNull_thenValidationFails() {
        NewCommentRequest dto = NewCommentRequest.builder()
                .text(null)
                .build();

        Set<ConstraintViolation<NewCommentRequest>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewCommentRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("text");
        assertThat(violation.getMessage()).isEqualTo("Комментарий не может быть пустым.");
    }

    @Test
    void whenTextIsOnlySpaces_thenValidationFails() {
        NewCommentRequest dto = NewCommentRequest.builder()
                .text("   ")
                .build();

        Set<ConstraintViolation<NewCommentRequest>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewCommentRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("text");
        assertThat(violation.getMessage()).isEqualTo("Комментарий не может быть пустым.");
    }
}
