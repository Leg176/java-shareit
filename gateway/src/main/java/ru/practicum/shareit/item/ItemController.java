package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import static ru.practicum.shareit.constants.HttpHeaders.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/items")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemClient.getItemsOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemBookingDateParametersDto(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @PathVariable @Positive(message = "itemId должен быть больше 0") Long itemId) {
        return itemClient.getItem(itemId, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchFilms(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                         @Valid @RequestBody @NotNull NewItemDto request) {
        return itemClient.addNewItem(ownerId, request);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody @NotNull UpdateItemDto request,
                                         @RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                         @PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        return itemClient.updateItem(request, ownerId, id);
    }

    @DeleteMapping("/{id}")
    public void removeItem(@PathVariable @Positive(message = "id должен быть больше 0") Long id,
                           @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        itemClient.deleteItem(id, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                         @PathVariable @Positive(message = "itemId должен быть больше 0") Long itemId,
                                         @Valid @RequestBody @NotNull NewCommentRequest request) {
        return itemClient.addNewComment(ownerId, itemId, request);
    }
}
