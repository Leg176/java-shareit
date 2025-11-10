package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

@Component
public class BookingMapper {
    public Booking mapToBooking(NewBookingRequest dto, User booker, Item item) {
        return Booking.builder()
                .item(item)
                .start(dto.getStart())
                .end(dto.getEnd())
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
}
