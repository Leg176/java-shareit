package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingRequest {
    @NotNull
    @Min(value = 1, message = "Id не может быть меньше 1.")
    Long id;
    @Future(message = "Дата должна быть в будущем")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate start;
    @Future(message = "Дата должна быть в будущем")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate end;
    private String status;

    public boolean hasDateStart() {
        return start != null;
    }

    public boolean hasDateEnd() {
        return end != null;
    }

    public boolean hasStatus() {
        return status != null && !status.isBlank();
    }
}
