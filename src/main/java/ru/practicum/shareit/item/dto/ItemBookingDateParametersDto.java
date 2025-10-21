package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemBookingDateParametersDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotBlank
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
    @NotNull
    private Boolean available;
    @NotBlank(message = "Имя не может быть пустым")
    private String owner;
    private Long requestId;
    private BookingTimeDto lastBooking;
    private BookingTimeDto nextBooking;
    @Builder.Default
    private List<CommentDto> comments = Collections.emptyList();
}
