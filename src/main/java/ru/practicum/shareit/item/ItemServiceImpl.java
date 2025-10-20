package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemBookingDateParametersDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> getItems() {
        return itemRepository.findAll().stream()
                .map(itemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> getItemsByOwner(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id: " + ownerId + " не найден");
        }
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        return items.stream().map(itemMapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto addNewItem(NewItemDto request, Long ownerId) {
        User owner = findByIdUser(ownerId);
        Item item = itemMapper.mapToItem(request, owner);
        itemRepository.save(item);
        return itemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long id) {
        Item item = findByIdItem(id);
        return itemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(UpdateItemDto request) {
        Item item = findByIdItem(request.getId());
        findByIdUser(request.getOwner());
        validationOwner(item, request.getOwner());
        itemMapper.updateItemFields(item, request);
        itemRepository.save(item);
        return itemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long id, Long ownerId) {
        Item item = findByIdItem(id);
        validationOwner(item, ownerId);
        itemRepository.delete(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String searchText = text.trim().toLowerCase();
        return itemRepository.searchAvailableItems(searchText).stream()
                .map(itemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBookingDateParametersDto> getUsersItemsWithBookingDates(Long ownerId) {
        // Получаем все вещи пользователя
        List<Item> usersItems = itemRepository.findByOwnerId(ownerId);

        return usersItems.stream()
                .map(item -> {
                    // Для каждой вещи выполняем запросы в репозиторий бронирований
                    Optional<Booking> lastBooking = bookingRepository.findLastBookingForItem(item.getId(),
                            BookingStatus.APPROVED);
                    Optional<Booking> nextBooking = bookingRepository.findNextBookingForItem(item.getId(),
                            BookingStatus.APPROVED, BookingStatus.WAITING);

                    // Вручную добавляем данные в DTO
                    return itemMapper.mapToItemBookingDateParametersDto(
                            item,
                            lastBooking.map(Booking::getEnd).orElse(null),
                            nextBooking.map(Booking::getStart).orElse(null)
                    );
                })
                .collect(Collectors.toList());
    }

    private Item findByIdItem(Long id) {
        Optional<Item> optItem = itemRepository.findById(id);
        if (optItem.isEmpty()) {
            throw new NotFoundException("Вещь с id: " + id + " в базе отсутствует");
        }
        return optItem.get();
    }

    private User findByIdUser(Long id) {
        Optional<User> optUser = userRepository.findById(id);
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
