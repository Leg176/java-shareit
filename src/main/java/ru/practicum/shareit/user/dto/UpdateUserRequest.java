package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Min(value = 1, message = "Id не может быть меньше 1.")
    private Long id;
    private String name;
    @Email(message = "Неверный формат электронной почты")
    private String email;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }
}
