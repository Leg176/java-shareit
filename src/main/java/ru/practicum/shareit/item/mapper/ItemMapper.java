package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item mapToItem(NewItemRequest request, User user) {
        if (request == null) {
            throw new IllegalArgumentException("Request не может быть пустым!");
        }
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(user)
                .available(request.getAvailable())
                .build();
    }

    public static Item mapToItem(NewItemRequest request, User user, Request itemRequest) {
        if (request == null) {
            throw new IllegalArgumentException("Request не может быть пустым!");
        }
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(user)
                .available(request.getAvailable())
                .request(itemRequest)
                .build();
    }

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getName())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request не может быть пустым!");
        }
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.isAvailability()) {
            item.setAvailable(request.getAvailable());
        }
        return item;
    }
}
