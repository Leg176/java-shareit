package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class UpdateItemRequestDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenValidData_thenNoViolations() {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(1L)
                .description("Test")
                .build();

        Set<ConstraintViolation<UpdateItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenDescriptionIsBlank_thenValidationFails() {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(1L)
                .description("")
                .build();

        Set<ConstraintViolation<UpdateItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("не должно быть пустым");
    }

    @Test
    void whenDescriptionIsNull_thenValidationFails() {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(1L)
                .description(null)
                .build();

        Set<ConstraintViolation<UpdateItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("не должно быть пустым");
    }

    @Test
    void whenDescriptionExceedsMaxLength_thenValidationFails() {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(1L)
                .description("a".repeat(201))
                .build();

        Set<ConstraintViolation<UpdateItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Описание не должно превышать 200 символов");
    }

    @Test
    void whenDescriptionIsExactlyMaxLength_thenNoViolations() {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(1L)
                .description("a".repeat(200))
                .build();

        Set<ConstraintViolation<UpdateItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenDescriptionIsOnlySpaces_thenNotBlankValidationFails() {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(1L)
                .description("   ")
                .build();

        Set<ConstraintViolation<UpdateItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<UpdateItemRequestDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("не должно быть пустым");
    }

    @Test
    void whenIdIsNull_thenNoViolations() {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(null)
                .description("Valid description")
                .build();

        Set<ConstraintViolation<UpdateItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenIdIsProvided_thenNoViolations() {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(1L)
                .description("Valid description")
                .build();

        Set<ConstraintViolation<UpdateItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenOnlyIdProvided_thenValidationFails() {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(1L)
                .description(null)
                .build();

        Set<ConstraintViolation<UpdateItemRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("не должно быть пустым");
    }
}