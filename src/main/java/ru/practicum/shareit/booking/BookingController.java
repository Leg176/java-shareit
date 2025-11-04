package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
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
    public Collection<BookingDto> findBookingsUser(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllBookingForOwner(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                                         @RequestParam(required = false,
                                                                 defaultValue = "ALL") String state) {
        return bookingService.getBookingsOwner(ownerId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable @Positive(message = "bookingId должен быть больше 0") Long bookingId,
                                 @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingDto create(@RequestHeader(X_SHARER_USER_ID) Long bookerId,
                             @Valid @RequestBody @NotNull NewBookingRequest request) {
        return bookingService.addNewBooking(request, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@RequestParam @NotNull Boolean approved,
                                          @RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                          @PathVariable @Positive(message = "bookingId должен быть больше 0")
                                          Long bookingId) {
        return bookingService.updateBookingStatus(bookingId, ownerId, approved);
    }

    @DeleteMapping("/{id}")
    public void removeBooking(@PathVariable @Positive(message = "id должен быть больше 0") Long id,
                              @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        bookingService.deleteBooking(id, ownerId);
    }
}
