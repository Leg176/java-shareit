package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemRequestDto {
    @NotNull
    @Min(value = 1, message = "Id не может быть меньше 1.")
    private Long id;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }
}
