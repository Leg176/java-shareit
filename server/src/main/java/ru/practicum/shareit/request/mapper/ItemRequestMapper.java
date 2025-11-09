package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {

    public ItemRequest mapToRequest(NewItemRequestDto request, User requestor) {
        return ItemRequest.builder()
                .description(request.getDescription())
                .requestor(requestor)
                .timeCreated(LocalDateTime.now())
                .build();
    }

    public ItemRequestDto mapToRequestDto(ItemRequest itemRequest) {
        List<ItemInItemRequestDto> itemsDto = Collections.emptyList();
        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            itemsDto = itemRequest.getItems().stream()
                    .map(this::mapToItemInRequestDto)
                    .collect(Collectors.toList());
        }

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor().getName())
                .timeCreated(itemRequest.getTimeCreated())
                .items(itemsDto)
                .build();
    }

    public ItemRequestWithoutItemsDto mapToRequestDtoNotItems(ItemRequest itemRequest) {
        return ItemRequestWithoutItemsDto.builder()
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

    private ItemInItemRequestDto mapToItemInRequestDto(Item item) {
        return ItemInItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
