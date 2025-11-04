package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingRequest {
    Long id;
    @FutureOrPresent(message = "Дата должна быть в будущем или настоящем")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @Future(message = "Дата должна быть в будущем")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
    private BookingStatus status;

    @AssertTrue(message = "Дата окончания должна быть после даты начала")
    public boolean isEndAfterStart() {
        if (start == null || end == null) {
            return true;
        }
        return end.isAfter(start);
    }

    public boolean hasDateStart() {
        return start != null;
    }

    public boolean hasDateEnd() {
        return end != null;
    }

    public boolean hasStatus() {
        return status != null;
    }
}
