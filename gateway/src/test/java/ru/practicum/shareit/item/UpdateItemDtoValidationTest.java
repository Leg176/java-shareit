package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateItemDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenValidData_thenNoViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .name("Updated Item")
                .description("Updated description")
                .available(true)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenOnlyNameProvided_thenNoViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .name("Updated Name Only")
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenOnlyDescriptionProvided_thenNoViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .description("Updated description only")
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenOnlyAvailableProvided_thenNoViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .available(false)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenIdIsSet_thenValidationFails() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .id(1L)
                .name("Updated Name")
                .description("Updated description")
                .available(true)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<UpdateItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("id");
        assertThat(violation.getMessage()).isEqualTo("id не может быть изменён");
    }

    @Test
    void whenOwnerIsSet_thenValidationFails() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .name("Updated Name")
                .description("Updated description")
                .available(true)
                .owner(1L)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<UpdateItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("owner");
        assertThat(violation.getMessage()).isEqualTo("Владелец не может быть изменён");
    }

    @Test
    void whenDescriptionExceedsMaxLength_thenValidationFails() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .name("Updated Name")
                .description("a".repeat(201))
                .available(true)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<UpdateItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Описание не должно превышать 200 символов");
    }

    @Test
    void whenDescriptionIsExactlyMaxLength_thenNoViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .name("Updated Name")
                .description("a".repeat(200))
                .available(true)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenNoFieldsProvided_thenAssertTrueValidationFails() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<UpdateItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("atLeastOneFieldPresent");
        assertThat(violation.getMessage()).isEqualTo("Хотя бы одно поле должно быть заполнено для обновления");
    }

    @Test
    void whenAllUpdateableFieldsAreNull_thenAssertTrueValidationFails() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .name(null)
                .description(null)
                .available(null)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<UpdateItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("atLeastOneFieldPresent");
        assertThat(violation.getMessage()).isEqualTo("Хотя бы одно поле должно быть заполнено для обновления");
    }

    @Test
    void whenIdAndOwnerAreNullButOtherFieldsPresent_thenNoViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .id(null) // Разрешено
                .name("Updated Name")
                .description(null)
                .available(null)
                .owner(null) // Разрешено
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenMultipleInvalidFields_thenMultipleViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .id(1L)
                .description("a".repeat(201))
                .owner(1L)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(3);

        List<String> propertyPaths = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toList());

        assertThat(propertyPaths).containsExactlyInAnyOrder("id", "description", "owner");
    }

    @Test
    void whenNameIsEmptyString_thenNoViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .name("")
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenDescriptionIsEmptyString_thenNoViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .description("")
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenNameIsOnlySpaces_thenNoViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .name("   ")
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenCombinationOfFieldsProvided_thenNoViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .name("New Name")
                .available(false)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenAllFieldsNull_thenAssertTrueValidationFails() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .id(null)
                .name(null)
                .description(null)
                .available(null)
                .owner(null)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);

        ConstraintViolation<UpdateItemDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("atLeastOneFieldPresent");
        assertThat(violation.getMessage()).isEqualTo("Хотя бы одно поле должно быть заполнено для обновления");
    }

    @Test
    void whenOnlyNullFieldsAndInvalidId_thenMultipleViolations() {
        UpdateItemDto dto = UpdateItemDto.builder()
                .id(1L)
                .name(null)
                .description(null)
                .available(null)
                .owner(null)
                .build();

        Set<ConstraintViolation<UpdateItemDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(2); // id + assertTrue

        List<String> propertyPaths = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toList());

        assertThat(propertyPaths).containsExactlyInAnyOrder("id", "atLeastOneFieldPresent");
    }
}