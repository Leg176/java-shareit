package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapper();

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private NewBookingRequest newBookingRequest;

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Jane")
                .email("jane@example.com")
                .build();

        item = Item.builder()
                .id(10L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void mapToBooking_shouldMapAllFields() {
        Booking result = bookingMapper.mapToBooking(newBookingRequest, booker, item);

        assertThat(result.getId()).isNull();
        assertThat(result.getStart()).isEqualTo(newBookingRequest.getStart());
        assertThat(result.getEnd()).isEqualTo(newBookingRequest.getEnd());
        assertThat(result.getBooker()).isEqualTo(booker);
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void mapToBooking_shouldSetWaitingStatus() {
        Booking result = bookingMapper.mapToBooking(newBookingRequest, booker, item);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void mapToBookingDto_shouldMapAllFields() {
        BookingDto result = bookingMapper.mapToBookingDto(booking);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(booking.getStart());
        assertThat(result.getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);

        assertThat(result.getItem().getId()).isEqualTo(10L);
        assertThat(result.getItem().getName()).isEqualTo("Drill");

        assertThat(result.getBooker().getId()).isEqualTo(1L);
    }

    @Test
    void mapToBookingDto_shouldHandleDifferentStatuses() {
        Booking approvedBooking = Booking.builder()
                .id(2L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();

        BookingDto result = bookingMapper.mapToBookingDto(approvedBooking);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void mapToBookingTimeDto_shouldMapAllFields() {
        BookingTimeDto result = bookingMapper.mapToBookingTimeDto(booking);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBookerId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(booking.getStart());
        assertThat(result.getEnd()).isEqualTo(booking.getEnd());
    }

    @Test
    void mapToBookingDto_shouldHandleAllBookingStatuses() {
        for (BookingStatus status : BookingStatus.values()) {
            Booking bookingWithStatus = Booking.builder()
                    .id(3L)
                    .item(item)
                    .booker(booker)
                    .start(LocalDateTime.now().plusDays(1))
                    .end(LocalDateTime.now().plusDays(2))
                    .status(status)
                    .build();

            BookingDto result = bookingMapper.mapToBookingDto(bookingWithStatus);

            assertThat(result.getStatus()).isEqualTo(status);
        }
    }

    @Test
    void mapToBookingTimeDto_shouldHandleDifferentBookers() {
        User differentBooker = User.builder()
                .id(5L)
                .name("Different Booker")
                .email("different@example.com")
                .build();

        Booking bookingWithDifferentBooker = Booking.builder()
                .id(4L)
                .item(item)
                .booker(differentBooker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        BookingTimeDto result = bookingMapper.mapToBookingTimeDto(bookingWithDifferentBooker);

        assertThat(result.getBookerId()).isEqualTo(5L);
    }

    @Test
    void mapToBookingDto_shouldHandleDifferentItems() {
        Item differentItem = Item.builder()
                .id(20L)
                .name("Hammer")
                .description("Steel hammer")
                .available(true)
                .owner(owner)
                .build();

        Booking bookingWithDifferentItem = Booking.builder()
                .id(5L)
                .item(differentItem)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        BookingDto result = bookingMapper.mapToBookingDto(bookingWithDifferentItem);

        assertThat(result.getItem().getId()).isEqualTo(20L);
        assertThat(result.getItem().getName()).isEqualTo("Hammer");
    }

    @Test
    void mapToBooking_shouldIgnoreItemIdFromRequest() {
        newBookingRequest.setItemId(999L);

        Booking result = bookingMapper.mapToBooking(newBookingRequest, booker, item);

        assertThat(result.getItem()).isEqualTo(item);
    }

    @Test
    void mapToBookingTimeDto_shouldMapCorrectTimeFields() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(3);

        Booking bookingWithSpecificTimes = Booking.builder()
                .id(6L)
                .item(item)
                .booker(booker)
                .start(startTime)
                .end(endTime)
                .status(BookingStatus.WAITING)
                .build();

        BookingTimeDto result = bookingMapper.mapToBookingTimeDto(bookingWithSpecificTimes);

        assertThat(result.getStart()).isEqualTo(startTime);
        assertThat(result.getEnd()).isEqualTo(endTime);
    }
}
