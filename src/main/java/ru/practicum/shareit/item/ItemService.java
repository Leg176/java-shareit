package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import java.util.Collection;
import java.util.List;

public interface ItemService {
    Collection<ItemDto> getItems();

    Collection<ItemDto> getItemsByOwner(Long ownerId);

    ItemDto addNewItem(NewItemDto request, Long ownerId);

    ItemDto updateItem(UpdateItemDto request);

    ItemDto getItemById(Long id);

    void deleteItem(Long id, Long ownerId);

    List<ItemDto> searchItems(String text);
}
