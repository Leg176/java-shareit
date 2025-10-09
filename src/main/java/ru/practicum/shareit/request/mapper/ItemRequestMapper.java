package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapper {
    public ItemRequest mapToRequest(NewItemRequestDto request, User requestor) {
        if (request == null) {
            throw new IllegalArgumentException("Request не может быть пустым!");
        }
        if (requestor == null) {
            throw new IllegalArgumentException("User (requestor) не может быть null");
        }
        return ItemRequest.builder()
                .description(request.getDescription())
                .requestor(requestor)
                .timeCreated(LocalDateTime.now())
                .build();
    }

    public ItemRequestDto mapToRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor().getName())
                .timeCreated(itemRequest.getTimeCreated())
                .build();
    }

    public ItemRequest updateRequestFields(ItemRequest request, UpdateItemRequestDto updateRequest) {
        if (updateRequest == null) {
            throw new IllegalArgumentException("UpdateRequest не может быть пустым!");
        }
        if (updateRequest.hasDescription()) {
            request.setDescription(updateRequest.getDescription());
        }
        return request;
    }
}
