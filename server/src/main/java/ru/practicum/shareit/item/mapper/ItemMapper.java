package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "owner", source = "owner.name")
    @Mapping(target = "requestId", source = "request.id")
    ItemDto mapToItemDto(Item item);

    @Mapping(target = "owner", source = "item.owner.name")
    @Mapping(target = "requestId", source = "item.request.id")
    @Mapping(target = "comments", source = "comments")
    ItemDto mapToItemAndCommentsDto(Item item, List<CommentDto> comments);


    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "owner", source = "item.owner.name")
    @Mapping(target = "requestId", source = "item.request.id")
    @Mapping(target = "lastBooking", source = "lastBooking")
    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "comments", source = "comments")
    ItemBookingDateParametersDto mapToItemBookingDateParametersDto(Item item,
                                                                   BookingTimeDto lastBooking,
                                                                   BookingTimeDto nextBooking,
                                                                   List<CommentDto> comments);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "owner", source = "item.owner.name")
    @Mapping(target = "requestId", source = "item.request.id")
    @Mapping(target = "comments", source = "commentDto")
    ItemBookingDateParametersDto mapToItemParametersDto(Item item, List<CommentDto> commentDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "name", source = "newItemDto.name")
    @Mapping(target = "description", source = "newItemDto.description")
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
