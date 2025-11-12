package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.NewItemDto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

class NewItemDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenValidData_thenNoViolations() {
        NewItemDto dto = NewItemDto.builder()
                .name("Test Item")
                .description("Test description for item")
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenValidDataWithRequestId_thenNoViolations() {
        NewItemDto dto = NewItemDto.builder()
                .name("Test Item")
                .description("Test description for item")
                .available(true)
                .requestId(1L)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenNameIsBlank_thenValidationFails() {
        NewItemDto dto = NewItemDto.builder()
                .name("")
                .description("Valid description")
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(2);

        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertThat(messages).contains("Название не может быть пустым");
    }

    @Test
    void whenNameIsNull_thenValidationFails() {
        NewItemDto dto = NewItemDto.builder()
                .name(null)
                .description("Valid description")
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Название не может быть пустым");
    }

    @Test
    void whenNameExceedsMaxLength_thenValidationFails() {
        NewItemDto dto = NewItemDto.builder()
                .name("a".repeat(51)) // 51 символов > 50
                .description("Valid description")
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Название должно быть от 1 до 50 символов");
    }

    @Test
    void whenNameIsExactlyMaxLength_thenNoViolations() {
        NewItemDto dto = NewItemDto.builder()
                .name("a".repeat(50)) // 50 символов = максимум
                .description("Valid description")
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenNameIsExactlyMinLength_thenNoViolations() {
        NewItemDto dto = NewItemDto.builder()
                .name("a") // 1 символ = минимум
                .description("Valid description")
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenDescriptionIsBlank_thenValidationFails() {
        NewItemDto dto = NewItemDto.builder()
                .name("Test Item")
                .description("")
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("не должно быть пустым");
    }

    @Test
    void whenDescriptionIsNull_thenValidationFails() {
        NewItemDto dto = NewItemDto.builder()
                .name("Test Item")
                .description(null)
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("не должно быть пустым");
    }

    @Test
    void whenDescriptionExceedsMaxLength_thenValidationFails() {
        NewItemDto dto = NewItemDto.builder()
                .name("Test Item")
                .description("a".repeat(201)) // 201 символов > 200
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Описание не должно превышать 200 символов");
    }

    @Test
    void whenDescriptionIsExactlyMaxLength_thenNoViolations() {
        NewItemDto dto = NewItemDto.builder()
                .name("Test Item")
                .description("a".repeat(200)) // 200 символов = максимум
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenAvailableIsNull_thenValidationFails() {
        NewItemDto dto = NewItemDto.builder()
                .name("Test Item")
                .description("Valid description")
                .available(null) // null не допускается
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("available");
        assertThat(violation.getMessage()).isEqualTo("не должно равняться null");
    }

    @Test
    void whenRequestIdIsNull_thenNoViolations() {
        NewItemDto dto = NewItemDto.builder()
                .name("Test Item")
                .description("Valid description")
                .available(true)
                .requestId(null) // requestId может быть null
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenMultipleFieldsInvalid_thenMultipleViolations() {
        NewItemDto dto = NewItemDto.builder()
                .name("")
                .description("")
                .available(null)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);

        List<String> distinctPropertyPaths = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .distinct()
                .collect(Collectors.toList());

        assertThat(distinctPropertyPaths).containsExactlyInAnyOrder("name", "description", "available");
        assertThat(violations).hasSize(4);
    }

    @Test
    void whenNameIsOnlySpaces_thenNotBlankValidationFails() {
        NewItemDto dto = NewItemDto.builder()
                .name("   ")
                .description("Valid description")
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Название не может быть пустым");
    }

    @Test
    void whenDescriptionIsOnlySpaces_thenNotBlankValidationFails() {
        NewItemDto dto = NewItemDto.builder()
                .name("Test Item")
                .description("   ")
                .available(true)
                .build();

        Set<ConstraintViolation<NewItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<NewItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("не должно быть пустым");
    }
}