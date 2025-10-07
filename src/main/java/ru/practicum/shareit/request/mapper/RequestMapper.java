package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static Request mapToRequest(NewRequest request, User requestor) {
        if (request == null) {
            throw new IllegalArgumentException("Request не может быть пустым!");
        }
        if (requestor == null) {
            throw new IllegalArgumentException("User (requestor) не может быть null");
        }
        return Request.builder()
                .description(request.getDescription())
                .requestor(requestor)
                .timeCreated(LocalDateTime.now())
                .build();
    }

    public static RequestDto mapToRequestDto(Request itemRequest) {
        return RequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor().getName())
                .timeCreated(itemRequest.getTimeCreated())
                .build();
    }

    public static Request updateRequestFields(Request request, UpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new IllegalArgumentException("UpdateRequest не может быть пустым!");
        }
        if (updateRequest.hasDescription()) {
            request.setDescription(updateRequest.getDescription());
        }
        return request;
    }
}
