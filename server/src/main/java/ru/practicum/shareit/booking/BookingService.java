package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.Collection;

public interface BookingService {
    Collection<BookingDto> getBookingsUser(Long userId, String state);

    Collection<BookingDto> getBookingsOwner(Long ownerId, String state);

    BookingDto addNewBooking(NewBookingRequest request, Long bookerId);

    BookingDto updateBookingStatus(Long bookingId, Long ownerId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    void deleteBooking(Long id, Long ownerId);
}
