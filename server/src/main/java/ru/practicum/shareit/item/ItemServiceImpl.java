package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.error.exceptions.BadRequestException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper;
    private final ItemRequestRepository itemRequestRepository;

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
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);

        Map<Long, List<Comment>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> {
                    List<Comment> itemComments = commentsByItemId.getOrDefault(item.getId(), Collections.emptyList());
                    List<CommentDto> commentDto = commentMapper.mapToCommentDtoList(itemComments);
                    return itemMapper.mapToItemAndCommentsDto(
                            item,
                            commentDto
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto addNewItem(NewItemDto request, Long ownerId) {
        User owner = findByIdUser(ownerId);
        if (request.getRequestId() != null) {
            ItemRequest itemRequest = findByIdRequest(request.getRequestId());
            Item item = itemMapper.mapToItem(request, owner, itemRequest);
            itemRepository.save(item);
            return itemMapper.mapToItemDto(item);
        } else {
            Item item = itemMapper.mapToItem(request, owner);
            itemRepository.save(item);
            return itemMapper.mapToItemDto(item);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemBookingDateParametersDto getItemById(Long itemId, Long ownerId) {
        Item item = findByIdItem(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentsDto = comments.stream()
                .map(commentMapper::mapToCommentDto)
                .toList();
        if (item.getOwner().getId().equals(ownerId)) {
        Optional<Booking> lastBooking = bookingRepository.findTopByItemIdAndStatusAndEndLessThanOrderByEndDesc(
                item.getId(), BookingStatus.APPROVED, LocalDateTime.now());
        Optional<Booking> nextBooking = bookingRepository.findTopByItemIdAndStatusAndStartGreaterThanOrderByStartAsc(
                item.getId(), BookingStatus.APPROVED, LocalDateTime.now());
        return itemMapper.mapToItemBookingDateParametersDto(item,
                lastBooking.map(bookingMapper::mapToBookingTimeDto).orElse(null),
                nextBooking.map(bookingMapper::mapToBookingTimeDto).orElse(null),
                commentsDto);
        }
            return itemMapper.mapToItemParametersDto(item, commentsDto);
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
    @Transactional
    public CommentDto addNewComment(Long authorId, Long itemId, NewCommentRequest request) {
        User author = findByIdUser(authorId);
        Item item = findByIdItem(itemId);
        BookingStatus status = BookingStatus.APPROVED;
        validateUserCanComment(authorId, itemId, status);
        Comment comment = commentMapper.mapToComment(request, author, item);
        commentRepository.save(comment);
        return commentMapper.mapToCommentDto(comment);
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

    private ItemRequest findByIdRequest(Long id) {
        Optional<ItemRequest> optItemRequest = itemRequestRepository.findById(id);
        if (optItemRequest.isEmpty()) {
            throw new NotFoundException("Запрос с id: " + id + " в базе отсутствует");
        }
        return optItemRequest.get();
    }

    private void validationOwner(Item item, Long ownerId) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Вы не являетесь владельцем, доступ запрещён!");
        }
    }

    private void validateUserCanComment(Long userId, Long itemId, BookingStatus status) {
        boolean hasCompletedBooking = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                userId, itemId, status);
        if (!hasCompletedBooking) {
            throw new BadRequestException("Оставлять комментарии можно только к арендованным вещам!");
        }
    }
}
