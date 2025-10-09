package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import java.util.Collection;

import static ru.practicum.shareit.constants.HttpHeaders.X_SHARER_USER_ID;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public Collection<BookingDto> findAll(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return bookingService.getBookingsByOwner(userId);
    }

    @PostMapping
    public BookingDto create(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                          @Valid @RequestBody @NotNull NewBookingRequest bookingRequest) {
        return bookingService.addNewBooking(bookingRequest, ownerId);
    }

    @PatchMapping("/{id}")
    public BookingDto update(@Valid @RequestBody @NotNull UpdateBookingRequest request,
                          @RequestHeader(X_SHARER_USER_ID) Long ownerId,
                          @PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        request.setId(id);
        return bookingService.updateBooking(request, ownerId);
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        return bookingService.getBookingById(id);
    }

    @DeleteMapping("/{id}")
    public void removeBooking(@PathVariable @Positive(message = "id должен быть больше 0") Long id,
                           @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        bookingService.deleteBooking(id, ownerId);
    }
}
