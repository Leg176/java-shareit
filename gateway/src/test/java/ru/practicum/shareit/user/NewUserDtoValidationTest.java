package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.NewUserDto;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class NewUserDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenValidData_thenNoViolations() {
        NewUserDto dto = NewUserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenNameIsBlank_thenValidationFails() {
        NewUserDto dto = NewUserDto.builder()
                .name("")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("не должно быть пустым");
    }

    @Test
    void whenEmailIsInvalid_thenValidationFails() {
        NewUserDto dto = NewUserDto.builder()
                .name("John Doe")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Неверный формат электронной почты");
    }

    @Test
    void whenEmailIsBlank_thenNotBlankValidationFails() {
        NewUserDto dto = NewUserDto.builder()
                .name("John Doe")
                .email("")
                .build();

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewUserDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("не должно быть пустым");
    }

    @Test
    void whenEmailIsInvalidFormat_thenEmailValidationFails() {
        NewUserDto dto = NewUserDto.builder()
                .name("John Doe")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewUserDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("Неверный формат электронной почты");
    }
}
