package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemDto {

    @Null(message = "id не может быть изменён")
    private Long id;
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
    private Boolean available;
    @Null(message = "Владелец не может быть изменён")
    private Long owner;

    @AssertTrue(message = "Хотя бы одно поле должно быть заполнено для обновления")
    public boolean isAtLeastOneFieldPresent() {
        return name != null || description != null || available != null;
    }
}
