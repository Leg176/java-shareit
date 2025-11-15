package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemRequestDto {
    private Long id;
    private String description;

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }
}
