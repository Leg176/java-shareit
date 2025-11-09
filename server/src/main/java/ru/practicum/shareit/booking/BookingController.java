package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public Collection<BookingDto> findBookingsUser(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                   @RequestParam String state,
                                                   @RequestParam Integer from,
                                                   @RequestParam Integer size) {
        return bookingService.getBookingsUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllBookingForOwner(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                                         @RequestParam String state,
                                                         @RequestParam Integer from,
                                                         @RequestParam Integer size) {
        return bookingService.getBookingsOwner(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingDto create(@RequestHeader(X_SHARER_USER_ID) Long bookerId,
                             @RequestBody NewBookingRequest request) {
        return bookingService.addNewBooking(request, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@PathVariable Long bookingId,
                                          @RequestParam Boolean approved,
                                          @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return bookingService.updateBookingStatus(bookingId, ownerId, approved);
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(
            @PathVariable Long id,
            @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        bookingService.deleteBooking(id, ownerId);
    }
}

