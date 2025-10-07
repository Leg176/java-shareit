package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository {
    Collection<Booking> findAll();

    Collection<Booking> getBookingsUser(Long ownerId);

    Booking create(Booking booking);

    Optional<Booking> findByBookingId(Long id);

    void delete(Long id);
}
