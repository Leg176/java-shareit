package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemDto {

    private Long id;
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
    private Boolean available;
    private Long owner;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean isAvailability() {
        return available != null;
    }
}
