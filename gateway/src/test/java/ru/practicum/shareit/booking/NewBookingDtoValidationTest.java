package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

class NewBookingDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenItemIdIsNull_thenViolation() {
        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .itemId(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(newBookingRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("не должно равняться null");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void whenItemIdIsLessThanOne_thenViolation(Long itemId) {
        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(newBookingRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Id не может быть меньше 1");
    }

    @Test
    void whenStartIsNull_thenViolation() {
        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(null)
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(newBookingRequest);
        assertThat(violations).hasSize(2);
    }

    @Test
    void whenStartIsInPast_thenViolation() {
        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(newBookingRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Дата должна быть в будущем или настоящем");
    }

    @Test
    void whenEndIsNull_thenViolation() {
        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(null)
                .build();

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(newBookingRequest);

        assertThat(violations).hasSize(2);

        List<String> propertyPaths = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toList());
        assertThat(propertyPaths).contains("end", "endAfterStart");
    }

    @Test
    void whenEndIsNotInFuture_thenViolation() {
        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(newBookingRequest);
        assertThat(violations).hasSize(2); // @Future и custom validation
    }

    @Test
    void whenEndIsBeforeStart_thenViolation() {
        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(newBookingRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Дата окончания должна быть после даты начала");
    }

    @Test
    void whenEndEqualsStart_thenViolation() {
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(sameTime)
                .end(sameTime)
                .build();

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(newBookingRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Дата окончания должна быть после даты начала");
    }

    @Test
    void whenMultipleInvalidFields_thenMultipleViolations() {
        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .itemId(-1L)
                .start(null)
                .end(LocalDateTime.now().minusDays(1))
                .build();

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(newBookingRequest);
        assertThat(violations).hasSizeGreaterThan(1);
    }
}