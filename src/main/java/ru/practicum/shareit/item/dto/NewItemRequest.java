package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewItemRequest {
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotBlank
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
    private Long requestId;
    @NotNull
    private Boolean available;
}
