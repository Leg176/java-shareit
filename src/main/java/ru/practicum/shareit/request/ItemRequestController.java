package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public ItemRequestDto create(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                 @Valid @RequestBody @NotNull NewItemRequestDto itemRequest) {
        return itemRequestService.addNewRequest(itemRequest, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemRequestDto update(@Valid @RequestBody @NotNull UpdateItemRequestDto request,
                                 @RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                 @PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        request.setId(id);
        return itemRequestService.updateRequest(request, ownerId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequest(@PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        return itemRequestService.getRequestById(id);
    }

    @DeleteMapping("/{id}")
    public void removeRequest(@PathVariable @Positive(message = "id должен быть больше 0") Long id,
                           @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        itemRequestService.deleteRequest(id, ownerId);
    }
}
