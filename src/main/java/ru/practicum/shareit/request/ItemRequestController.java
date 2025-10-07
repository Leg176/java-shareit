package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.UpdateRequest;
import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;

    @Autowired
    public ItemRequestController( RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public Collection<RequestDto> findAll(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId != null) {
            return requestService.getRequestsByOwner(userId);
        }
        return requestService.getRequests();
    }

    @PostMapping
    public RequestDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @Valid @RequestBody @NotNull NewRequest itemRequest) {
        return requestService.addNewRequest(itemRequest, ownerId);
    }

    @PatchMapping("/{id}")
    public RequestDto update(@Valid @RequestBody @NotNull UpdateRequest request,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        request.setId(id);
        return requestService.updateRequest(request, ownerId);
    }

    @GetMapping("/{id}")
    public RequestDto getRequest(@PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        return requestService.getRequestById(id);
    }

    @DeleteMapping("/{id}")
    public void removeRequest(@PathVariable @Positive(message = "id должен быть больше 0") Long id,
                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        requestService.deleteRequest(id, ownerId);
    }
}
