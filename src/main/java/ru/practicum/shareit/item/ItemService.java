package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    Collection<ItemDto> getItems();

    Collection<ItemDto> getItemsByOwner(Long ownerId);

    ItemDto addNewItem(NewItemDto request, Long ownerId);

    ItemDto updateItem(UpdateItemDto request);

    ItemBookingDateParametersDto getItemById(Long itemId, Long ownerId);

    void deleteItem(Long id, Long ownerId);

    List<ItemDto> searchItems(String text);

    List<ItemBookingDateParametersDto> getUsersItemsWithBookingDates(Long ownerId);

    CommentDto addNewComment(Long ownerId, Long itemId, NewCommentRequest request);
}
