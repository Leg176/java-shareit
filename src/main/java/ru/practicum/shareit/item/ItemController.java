package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import java.util.Collection;
import java.util.List;
import static ru.practicum.shareit.constants.HttpHeaders.X_SHARER_USER_ID;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                          @Valid @RequestBody @NotNull NewItemDto itemRequest) {
        return itemService.addNewItem(itemRequest, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@Valid @RequestBody @NotNull UpdateItemDto request,
                          @RequestHeader(X_SHARER_USER_ID) Long ownerId,
                          @PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        request.setId(id);
        request.setOwner(ownerId);
        return itemService.updateItem(request);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        return itemService.getItemById(id);
    }

    @DeleteMapping("/{id}")
    public void removeItem(@PathVariable @Positive(message = "id должен быть больше 0") Long id,
                           @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        itemService.deleteItem(id, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchFilms(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
