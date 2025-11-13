package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingClient.API_PREFIX;
import static ru.practicum.shareit.constants.HttpHeaders.X_SHARER_USER_ID;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    private BookingClient bookingClient;
    private static final String BASE_URL = "http://test-server";

    @BeforeEach
    void setUp() {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(BASE_URL + API_PREFIX);
        when(builder.uriTemplateHandler(any(UriTemplateHandler.class)))
                .thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class)))
                .thenReturn(builder);
        when(builder.build())
                .thenReturn(restTemplate);
        builder.uriTemplateHandler(uriBuilderFactory);

        bookingClient = new BookingClient(BASE_URL, builder);

        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body("mock-response");
        lenient().when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                eq(Object.class)
        )).thenReturn(mockResponse);

        lenient().when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                eq(Object.class),
                any(Map.class)
        )).thenReturn(mockResponse);
    }

    @Test
    void getBookings_ShouldCallGetWithParameters() {
        Long userId = 1L;
        BookingState state = BookingState.ALL;

        bookingClient.getBookings(userId, state);

        verify(restTemplate).exchange(
                eq("?state={state}"),
                eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class),
                eq(Map.of("state", "ALL"))
        );
    }

    @Test
    void getBooking_ShouldCallGetWithBookingIdAndUserIdHeader() {
        Long userId = 1L;
        Long bookingId = 1L;

        bookingClient.getBooking(userId, bookingId);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }

    @Test
    void getBookingsOwner_ShouldCallGetWithParametersAndUserIdHeader() {
        Long ownerId = 1L;
        BookingState state = BookingState.CURRENT;

        bookingClient.getBookingsOwner(ownerId, state);

        verify(restTemplate).exchange(
                eq("/owner?state={state}"),
                eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class),
                eq(Map.of("state", "CURRENT"))
        );
    }

    @Test
    void addNewBooking_ShouldCallPostWithBookingDataAndUserIdHeader() {
        Long ownerId = 1L;
        NewBookingRequest newBookingRequest = new NewBookingRequest();
        newBookingRequest.setItemId(1L);
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        bookingClient.bookItem(ownerId, newBookingRequest);

        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == newBookingRequest &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }

    @Test
    void updateBookingStatus_ShouldCallPatchWithApprovedParameter() {
        Long ownerId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                eq(Object.class),
                any(Map.class)
        )).thenReturn(ResponseEntity.ok().body("test"));

        bookingClient.updateBookingStatus(bookingId, ownerId, approved);

        verify(restTemplate).exchange(
                eq("/1?approved={approved}"),
                eq(HttpMethod.PATCH),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class),
                eq(Map.of("approved", true))
        );
    }

    @Test
    void deleteBooking_ShouldCallDeleteWithIdInPath() {
        Long ownerId = 1L;
        Long bookingId = 1L;
        bookingClient.deleteBooking(bookingId, ownerId);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.DELETE),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }
}
