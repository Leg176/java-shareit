package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.error.exceptions.BadRequestException;
import ru.practicum.shareit.error.exceptions.ForbiddenException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> getBookingsUser(Long userId, String state, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " в базе отсутствует");
        }
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Передано некорректное значение");
        }

        Collection<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case CURRENT ->
                    bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualAndStatusInOrderByStartDesc(
                            userId, LocalDateTime.now(), LocalDateTime.now(), Arrays.asList(BookingStatus.WAITING, BookingStatus.APPROVED));
            case PAST -> bookingRepository.findByBookerIdAndEndLessThanAndStatusOrderByStartDesc(
                    userId, LocalDateTime.now(), BookingStatus.APPROVED);
            case FUTURE -> bookingRepository.findByBookerIdAndStartGreaterThanAndStatusInOrderByStartDesc(
                    userId, LocalDateTime.now(), Arrays.asList(BookingStatus.WAITING, BookingStatus.APPROVED));
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusInOrderByStartDesc(userId,
                    Arrays.asList(BookingStatus.REJECTED, BookingStatus.CANCELED));
        };
        return bookings.stream()
                .skip(from)
                .limit(size)
                .map(bookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> getBookingsOwner(Long ownerId, String state, Integer from, Integer size) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id: " + ownerId + " в базе отсутствует");
        }
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Передан некорректное значение");
        }
        Collection<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT ->
                    bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualAndStatusInOrderByStartDesc(
                            ownerId, LocalDateTime.now(), LocalDateTime.now(), Arrays.asList(BookingStatus.WAITING, BookingStatus.APPROVED));
            case PAST -> bookingRepository.findByItemOwnerIdAndEndLessThanAndStatusOrderByStartDesc(
                    ownerId, LocalDateTime.now(), BookingStatus.APPROVED);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartGreaterThanAndStatusInOrderByStartDesc(
                    ownerId, LocalDateTime.now(), Arrays.asList(BookingStatus.WAITING, BookingStatus.APPROVED));
            case WAITING ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusInOrderByStartDesc(ownerId,
                    Arrays.asList(BookingStatus.REJECTED, BookingStatus.CANCELED));
        };
        return bookings.stream()
                .skip(from)
                .limit(size)
                .map(bookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto addNewBooking(NewBookingRequest request, Long bookerId) {
        User user = findByIdUser(bookerId);
        Item item = findByIdItem(request.getItemId());
        validateItemOwnerIsNotBooker(item, bookerId);
        isAvailable(item);
        intersectionBooking(item, request);
        if (request.getEnd().isBefore(request.getStart())) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала");
        }
        Booking booking = bookingMapper.mapToBooking(request, user, item);
        bookingRepository.save(booking);
        return bookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = findByIdBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Вы не являетесь владельцем вещи!");
        }
        validateStatus(booking);
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        updateBookingStatus(booking, status);
        bookingRepository.save(booking);
        return bookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = findByIdBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new ForbiddenException("Просмотр запрещён!");
        }
        return bookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id, Long userId) {
        Booking booking = findByIdBooking(id);
        validationBooker(booking, userId);
        bookingRepository.delete(booking);
    }

    private void validateStatus(Booking booking) {
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException(
                    "Нельзя изменить статус бронирования. Текущий статус: " + booking.getStatus()
            );
        }
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

    private void validationBooker(Booking booking, Long userId) {
        if (!booking.getBooker().getId().equals(userId)) {
            throw new ForbiddenException("Вы не являетесь владельцем, доступ запрещён!");
        }
    }

    private void validateItemOwnerIsNotBooker(Item item, Long bookerId) {
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationException("Владелец вещи не может создавать запрос на её бронирование");
        }
    }

    private void isAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }
    }

    private void intersectionBooking(Item item, NewBookingRequest request) {
        if (bookingRepository.existsOverlappingBookings(item.getId(), request.getStart(), request.getEnd(),
                BookingStatus.APPROVED)) {
            throw new ValidationException("На выбранные даты вещь уже забронирована");
        }
    }

    private Booking findByIdBooking(Long id) {
        Optional<Booking> optBooking = bookingRepository.findById(id);
        if (optBooking.isEmpty()) {
            throw new NotFoundException("Бронирование с id: " + id + " в базе отсутствует");
        }
        return optBooking.get();
    }

    private void updateBookingStatus(Booking booking, BookingStatus status) {
        if (booking == null) {
            throw new IllegalArgumentException("Бронирование не может быть пустым");
        }
        booking.setStatus(status);
    }
}

