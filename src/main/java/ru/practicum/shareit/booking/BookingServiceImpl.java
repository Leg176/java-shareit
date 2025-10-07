package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Collection<BookingDto> getBookings() {
        return bookingRepository.findAll().stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getBookingsByOwner(Long ownerId) {
        validationUser(ownerId);
        return bookingRepository.getBookingsUser(ownerId).stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto addNewBooking(NewBookingRequest request, Long ownerId) {
        User user = validationUser(ownerId);
        Item item = validationItem(request.getItemId());
        if (request.getEnd().isBefore(request.getStart())) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала");
        }
        Booking booking = BookingMapper.mapToBooking(request, user, item);
        bookingRepository.create(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto updateBooking(UpdateBookingRequest request, Long ownerId) {
        Booking booking = validationBooking(request.getId());
        validationUser(ownerId);
        validationOwner(booking, ownerId);
        if (request.getEnd().isBefore(request.getStart())) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала");
        }
        BookingMapper.updateBookingFields(booking, request);
        bookingRepository.create(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long id) {
        Booking booking = validationBooking(id);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public void deleteBooking(Long id, Long ownerId) {
        Booking booking = validationBooking(id);
        validationUser(ownerId);
        validationOwner(booking, ownerId);
        bookingRepository.delete(id);
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

    private void validationOwner(Booking booking, Long ownerId) {
        if (!booking.getBooker().getId().equals(ownerId)) {
            throw new ValidationException("Вы не являетесь владельцем, доступ запрещён!");
        }
    }
    private Booking validationBooking(Long id) {
         Optional<Booking> optBooking = bookingRepository.findByBookingId(id);
         if (optBooking.isEmpty()) {
             throw new NotFoundException("Бронирование с id: " + id + " в базе отсутствует");
         }
         return optBooking.get();
    }
}

