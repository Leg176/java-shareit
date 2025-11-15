package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class UpdateUserDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenValidData_thenNoViolations() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenPartialData_thenNoViolations() {
        // Можно обновлять только часть полей
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(1L)
                .name("John Doe")
                .build();

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenOnlyId_thenNoViolations() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(1L)
                .build();

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenEmailIsInvalid_thenValidationFails() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<UpdateUserDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("Неверный формат электронной почты");
    }

    @Test
    void whenEmailIsNull_thenNoViolations() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(1L)
                .name("John Doe")
                .email(null)
                .build();

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenEmailIsOnlySpaces_thenValidationFails() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("   ")
                .build();

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<UpdateUserDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("Неверный формат электронной почты");
    }

    @Test
    void whenNameIsNull_thenNoViolations() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(1L)
                .name(null)
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenIdIsNull_thenNoViolations() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(null)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}