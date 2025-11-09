package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestWithoutItemsDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import java.util.Collection;

import static ru.practicum.shareit.constants.HttpHeaders.X_SHARER_USER_ID;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public Collection<ItemRequestDto> findAll(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestService.getRequestsByOwner(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequest(@PathVariable Long id) {
        return itemRequestService.getRequestById(id);
    }

    @GetMapping("/all")
    public Collection<ItemRequestWithoutItemsDto> getRequestNotOwner(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestService.getRequestsByNotOwner(userId);
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                 @RequestBody NewItemRequestDto itemRequest) {
        return itemRequestService.addNewRequest(itemRequest, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemRequestDto update(@RequestBody UpdateItemRequestDto request,
                                 @RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                 @PathVariable Long id) {
        request.setId(id);
        return itemRequestService.updateRequest(request, ownerId);
    }

    @DeleteMapping("/{id}")
    public void removeRequest(@PathVariable Long id,
                           @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        itemRequestService.deleteRequest(id, ownerId);
    }
}
