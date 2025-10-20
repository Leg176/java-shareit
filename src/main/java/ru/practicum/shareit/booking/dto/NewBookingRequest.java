package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewBookingRequest {
    @NotNull
    @Min(value = 1, message = "Id не может быть меньше 1.")
    private Long itemId;
    @NotNull
    @FutureOrPresent(message = "Дата должна быть в будущем или настоящем")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @NotNull
    @Future(message = "Дата должна быть в будущем")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    @AssertTrue(message = "Дата окончания должна быть после даты начала")
    public boolean isEndAfterStart() {
        return start != null && end != null && end.isAfter(start);
    }
}
