package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public Booking mapToBooking(NewBookingRequest request, User booker, Item item) {
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
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(BookingItemDto.builder()
                        .name(booking.getItem().getName())
                        .id(booking.getItem().getId())
                        .build())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(BookingBookerDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .status(booking.getStatus())
                .build();
    }

    public BookingTimeDto mapToBookingTimeDto(Booking booking) {
        return BookingTimeDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .build();
    }

    public void updateBookingStatus(Booking booking, BookingStatus status) {
        if (booking == null) {
            throw new IllegalArgumentException("Бронирование не может быть пустым");
        }
        booking.setStatus(status);
    }
}
