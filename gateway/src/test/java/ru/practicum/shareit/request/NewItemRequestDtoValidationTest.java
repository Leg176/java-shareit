package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class NewItemRequestDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenValidData_thenNoViolations() {
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description("TestTestTestTest")
                .build();

        Set<ConstraintViolation<NewItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenDescriptionIsBlank_thenValidationFails() {
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description("")
                .build();

        Set<ConstraintViolation<NewItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Описание не может быть пустым");
    }

    @Test
    void whenDescriptionIsNull_thenValidationFails() {
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description(null)
                .build();

        Set<ConstraintViolation<NewItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Описание не может быть пустым");
    }

    @Test
    void whenDescriptionExceedsMaxLength_thenValidationFails() {
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description("a".repeat(201))
                .build();

        Set<ConstraintViolation<NewItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Описание не должно превышать 200 символов");
    }

    @Test
    void whenDescriptionIsExactlyMaxLength_thenNoViolations() {
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description("a".repeat(200))
                .build();

        Set<ConstraintViolation<NewItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenDescriptionIsOnlySpaces_thenNotBlankValidationFails() {
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description("   ")
                .build();

        Set<ConstraintViolation<NewItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewItemRequestDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Описание не может быть пустым");
    }
}