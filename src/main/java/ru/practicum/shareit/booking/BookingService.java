package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import java.util.Collection;

public interface BookingService {
    Collection<BookingDto> getBookings();

    Collection<BookingDto> getBookingsByOwner(Long ownerId);

    BookingDto addNewBooking(NewBookingRequest request, Long ownerId);

    BookingDto updateBooking(UpdateBookingRequest request, Long ownerId);

    BookingDto getBookingById(Long id);

    void deleteBooking(Long id, Long ownerId);
}
