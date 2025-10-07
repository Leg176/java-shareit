package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking mapToBooking(NewBookingRequest request, User booker, Item item) {
        if (request == null) {
            throw new IllegalArgumentException("Request не может быть пустым!");
        }
        if (booker == null) {
            throw new IllegalArgumentException("User (booker) не может быть null");
        }
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null");
        }
        return Booking.builder()
                .item(item)
                .start(request.getStart())
                .end(request.getEnd())
                .booker(booker)
                .status(checkBookingStatus(request.getStatus()))
                .build();
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker().getName())
                .status(booking.getStatus().name())
                .build();
    }

    public static Booking updateBookingFields(Booking booking, UpdateBookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request не может быть пустым!");
        }
        if (booking == null) {
            throw new IllegalArgumentException("Бронирование не может быть пустым");
        }
        if (request.hasDateStart()) {
            booking.setStart(request.getStart());
        }
        if (request.hasDateEnd()) {
            booking.setEnd(request.getEnd());
        }
        if (request.hasStatus()) {
            booking.setStatus(checkBookingStatus(request.getStatus()));
        }
        return booking;
    }

    private static BookingStatus checkBookingStatus(String status) {
        if (status == null || status.isBlank()) {
            return BookingStatus.WAITING;
        }
        try {
            return BookingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный статус бронирования: " + status);
        }
    }
}
