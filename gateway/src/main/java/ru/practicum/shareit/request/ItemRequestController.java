package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import static ru.practicum.shareit.constants.HttpHeaders.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemRequestClient.getRequestsByOwner(ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequest(@PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        return itemRequestClient.getRequestById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestNotOwner(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.getRequestsByNotOwner(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                         @Valid @RequestBody @NotNull NewItemRequestDto itemRequest) {
        return itemRequestClient.addNewRequest(itemRequest, ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody @NotNull UpdateItemRequestDto request,
                                         @RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                         @PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        return itemRequestClient.updateRequest(request, ownerId, id);
    }

    @DeleteMapping("/{id}")
    public void removeRequest(@PathVariable @Positive(message = "id должен быть больше 0") Long id,
                              @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        itemRequestClient.deleteRequest(id, ownerId);
    }
}
