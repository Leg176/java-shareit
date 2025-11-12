package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewItemRequestDto {
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
}
