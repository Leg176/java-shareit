package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import java.util.Collection;
import java.util.List;

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
    public Collection<ItemDto> findAll(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId != null) {
            return itemService.getItemsByOwner(userId);
        }
        return itemService.getItems();
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @Valid @RequestBody @NotNull NewItemRequest itemRequest) {
        return itemService.addNewItem(itemRequest, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@Valid @RequestBody @NotNull UpdateItemRequest request,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId,
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
                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        itemService.deleteItem(id, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchFilms(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
