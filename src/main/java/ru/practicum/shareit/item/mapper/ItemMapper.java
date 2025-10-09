package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "owner", source = "owner.name")
    @Mapping(target = "requestId", source = "request.id")
    ItemDto mapToItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "name", source = "newItemDto.name")
    @Mapping(target = "owner", source = "user")
    Item mapToItem(NewItemDto newItemDto, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "newItemDto.name")
    @Mapping(target = "description", source = "newItemDto.description")
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "request", source = "itemRequest")
    Item mapToItem(NewItemDto newItemDto, User user, ItemRequest itemRequest);

    default Item updateItemFields(@MappingTarget Item item, UpdateItemDto request) {
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
