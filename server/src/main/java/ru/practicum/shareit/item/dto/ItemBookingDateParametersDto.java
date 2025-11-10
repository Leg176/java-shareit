package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String name;
    private String description;
    private Boolean available;
    private String owner;
    private Long requestId;
    private BookingTimeDto lastBooking;
    private BookingTimeDto nextBooking;
    @Builder.Default
    private List<CommentDto> comments = Collections.emptyList();
}
