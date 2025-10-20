package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
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
    public Collection<BookingDto> getBookingsUser(Long userId, String state) {
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
            case ALL -> bookingRepository.findByBookerId(userId);
            case CURRENT -> bookingRepository.findCurrentBookingsByBookerId(userId);
            case PAST -> bookingRepository.findPastBookingsByBookerId(userId);
            case FUTURE -> bookingRepository.findFutureBookingsByBookerId(userId);
            case WAITING -> bookingRepository.findWaitingBookingsByBookerId(userId);
            case REJECTED -> bookingRepository.findRejectedBookingsByBookerId(userId);
        };
        return bookings.stream()
                .map(bookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> getBookingsOwner(Long ownerId, String state) {
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
            case ALL -> bookingRepository.findAllBookingsForOwner(ownerId);
            case CURRENT -> bookingRepository.findCurrentBookingsForOwner(ownerId);
            case PAST -> bookingRepository.findPastBookingsForOwner(ownerId);
            case FUTURE -> bookingRepository.findFutureBookingsForOwner(ownerId);
            case WAITING -> bookingRepository.findWaitingBookingsForOwner(ownerId);
            case REJECTED -> bookingRepository.findRejectedBookingsForOwner(ownerId);
        };
        return bookings.stream()
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
        bookingMapper.updateBookingStatus(booking, status);
        bookingRepository.save(booking);
        return bookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = findByIdBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new ValidationException("Просмотр запрещён!");
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
        if (bookingRepository.existsOverlappingBookings(item.getId(), request.getStart(), request.getEnd())) {
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
}

