package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewItemDto {
    private String name;
    private String description;
    private Long requestId;
    private Boolean available;
}
