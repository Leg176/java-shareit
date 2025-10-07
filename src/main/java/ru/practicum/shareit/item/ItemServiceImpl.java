package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> getItems() {
        return itemRepository.findAll().stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public  Collection<ItemDto> getItemsByOwner(Long ownerId) {
        validationUser(ownerId);
        return itemRepository.getItemsUser(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addNewItem(NewItemRequest request, Long ownerId) {
        User owner = validationUser(ownerId);
        Item item = ItemMapper.mapToItem(request, owner);
        itemRepository.create(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = validationItem(id);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(UpdateItemRequest request) {
        Item item = validationItem(request.getId());
        validationUser(request.getOwner());
        validationOwner(item, request.getOwner());
        ItemMapper.updateItemFields(item, request);
        itemRepository.create(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public void deleteItem(Long id, Long ownerId) {
        Item item = validationItem(id);
        validationUser(ownerId);
        validationOwner(item, ownerId);
        itemRepository.delete(id);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchAvailableFilms(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    private Item validationItem(Long id) {
        Optional<Item> optItem = itemRepository.findByItemId(id);
        if (optItem.isEmpty()) {
            throw new NotFoundException("Вещь с id: " + id + " в базе отсутствует");
        }
        return optItem.get();
    }

    private User validationUser(Long id) {
        Optional<User> optUser = userRepository.findByUserId(id);
        if (optUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + id + " в базе отсутствует");
        }
        return optUser.get();
    }

    private void validationOwner(Item item, Long ownerId) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Вы не являетесь владельцем, доступ запрещён!");
        }
    }
}
