package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.error.ErrorHandler;
import java.net.URI;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(ErrorHandler.class)
class BookingControllerTest {

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController bookingController;

    private NewBookingRequest newBookingRequest;

    @BeforeEach
    void setUp() {
        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setItemId(1L);
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void getBookings_ShouldCallClientWithConvertedState() {
        Long userId = 1L;
        String stateParam = "current";

        when(bookingClient.getBookings(userId, BookingState.CURRENT))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<Object> result = bookingController.getBookings(userId, stateParam);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(bookingClient).getBookings(userId, BookingState.CURRENT);
    }

    @Test
    void findAllBookingForOwner_ShouldReturnBookingsForOwner() {
        Long ownerId = 1L;
        String stateParam = "ALL";
        Integer from = 0;
        Integer size = 10;
        String mockResponse = "[{\"id\":1,\"status\":\"APPROVED\"},{\"id\":2,\"status\":\"WAITING\"}]";

        when(bookingClient.getBookingsOwner(ownerId, BookingState.ALL))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = bookingController.findAllBookingForOwner(ownerId, stateParam);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(bookingClient).getBookingsOwner(ownerId, BookingState.ALL);
    }

    @Test
    void getBooking_ShouldReturnBookingById() {
        Long userId = 1L;
        Long bookingId = 100L;
        String mockResponse = "{\"id\":100,\"status\":\"APPROVED\",\"booker\":{\"id\":2}}";

        when(bookingClient.getBooking(userId, bookingId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = bookingController.getBooking(userId, bookingId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(bookingClient).getBooking(userId, bookingId);
    }

    @Test
    void create_ShouldCreateNewBooking() {
        Long userId = 1L;
        String mockResponse = "{\"id\":50,\"status\":\"WAITING\",\"itemId\":10}";

        when(bookingClient.bookItem(userId, newBookingRequest))
                .thenReturn(ResponseEntity.created(URI.create("/bookings/50")).body(mockResponse));

        ResponseEntity<Object> result = bookingController.create(userId, newBookingRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getHeaders().getLocation()).hasPath("/bookings/50");
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(bookingClient).bookItem(userId, newBookingRequest);
    }

    @Test
    void updateBookingStatus_WhenApproved_ShouldUpdateStatus() {
        Long ownerId = 1L;
        Long bookingId = 100L;
        Boolean approved = true;
        String mockResponse = "{\"id\":100,\"status\":\"APPROVED\"}";

        when(bookingClient.updateBookingStatus(bookingId, ownerId, approved))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = bookingController.updateBookingStatus(approved, ownerId, bookingId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(bookingClient).updateBookingStatus(bookingId, ownerId, approved);
    }

    @Test
    void removeBooking_ShouldDeleteBooking() {
        Long id = 100L;
        Long ownerId = 1L;

        when(bookingClient.deleteBooking(id, ownerId))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Object> result = bookingController.removeBooking(id, ownerId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(bookingClient).deleteBooking(id, ownerId);
    }
}
