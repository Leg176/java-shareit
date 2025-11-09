package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewItemDto {
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 1, max = 50, message = "Название должно быть от 1 до 50 символов")
    private String name;
    @NotBlank
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
    private Long requestId;
    @NotNull
    private Boolean available;
}
