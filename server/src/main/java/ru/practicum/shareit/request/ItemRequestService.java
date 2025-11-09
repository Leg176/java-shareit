package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithoutItemsDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import java.util.Collection;

public interface ItemRequestService {

    Collection<ItemRequestDto> getRequestsByOwner(Long ownerId);

    Collection<ItemRequestWithoutItemsDto> getRequestsByNotOwner(Long ownerId);

    ItemRequestDto addNewRequest(NewItemRequestDto request, Long ownerId);

    ItemRequestDto updateRequest(UpdateItemRequestDto request, Long ownerId);

    ItemRequestDto getRequestById(Long id);

    void deleteRequest(Long id, Long ownerId);
}
