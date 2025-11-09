package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentRequest {
    @NotBlank(message = "Комментарий не может быть пустым.")
    private String text;
}
