package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapper {
    public Item mapToItem(NewItemDto request, User user) {
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

    public Item mapToItem(NewItemDto request, User user, ItemRequest itemRequest) {
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

    public ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getName())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public Item updateItemFields(Item item, UpdateItemDto request) {
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
