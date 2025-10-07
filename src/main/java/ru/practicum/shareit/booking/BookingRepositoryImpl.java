package ru.practicum.shareit.booking;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BookingRepositoryImpl implements BookingRepository {
    private final Map<Long, Booking> bookings = new HashMap<>();

    public Collection<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    public Collection<Booking> getBookingsUser(Long ownerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public Booking create(Booking booking) {
        if(booking.getId() == null || booking.getId() == 0) {
            Long id = getNextId();
            booking.setId(id);
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public Optional<Booking> findByBookingId(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    public void delete(Long id) {
        bookings.remove(id);
    }

    private long getNextId() {
        long currentMaxId = bookings.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

