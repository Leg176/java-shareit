package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl service;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private NewBookingRequest newBookingRequest;

    @BeforeEach
    void setup() {
        booker = User.builder()
                .id(1L)
                .name("booker")
                .email("booker@mail.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@mail.com")
                .build();

        item = Item.builder()
                .id(10L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(100L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(100L)
                .item(BookingItemDto.builder().id(10L).name("Drill").build())
                .booker(BookingBookerDto.builder().id(1L).build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setItemId(10L);
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void getBookingsUser_allState_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsUser(1L, "ALL");

        assertEquals(1, result.size());
        verify(userRepository).existsById(1L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(1L);
    }

    @Test
    void getBookingsUser_currentState_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualAndStatusInOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsUser(1L, "CURRENT");

        assertEquals(1, result.size());
        verify(bookingRepository).findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualAndStatusInOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), any());
    }

    @Test
    void getBookingsUser_pastState_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndLessThanAndStatusOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsUser(1L, "PAST");

        assertEquals(1, result.size());
        verify(bookingRepository).findByBookerIdAndEndLessThanAndStatusOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED));
    }

    @Test
    void getBookingsUser_futureState_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartGreaterThanAndStatusInOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsUser(1L, "FUTURE");

        assertEquals(1, result.size());
        verify(bookingRepository).findByBookerIdAndStartGreaterThanAndStatusInOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), any());
    }

    @Test
    void getBookingsUser_waitingState_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsUser(1L, "WAITING");

        assertEquals(1, result.size());
        verify(bookingRepository).findByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING);
    }

    @Test
    void getBookingsUser_rejectedState_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusInOrderByStartDesc(eq(1L), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsUser(1L, "REJECTED");

        assertEquals(1, result.size());
        verify(bookingRepository).findByBookerIdAndStatusInOrderByStartDesc(eq(1L), any());
    }

    @Test
    void getBookingsOwner_allState_success() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(2L)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsOwner(2L, "ALL");

        assertEquals(1, result.size());
        verify(userRepository).existsById(2L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(2L);
    }

    @Test
    void getBookingsOwner_currentState_success() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualAndStatusInOrderByStartDesc(
                eq(2L), any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsOwner(2L, "CURRENT");

        assertEquals(1, result.size());
        verify(bookingRepository).findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualAndStatusInOrderByStartDesc(
                eq(2L), any(LocalDateTime.class), any(LocalDateTime.class), any());
    }

    @Test
    void getBookingsOwner_pastState_success() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndEndLessThanAndStatusOrderByStartDesc(
                eq(2L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsOwner(2L, "PAST");

        assertEquals(1, result.size());
        verify(bookingRepository).findByItemOwnerIdAndEndLessThanAndStatusOrderByStartDesc(
                eq(2L), any(LocalDateTime.class), eq(BookingStatus.APPROVED));
    }

    @Test
    void getBookingsOwner_futureState_success() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStartGreaterThanAndStatusInOrderByStartDesc(
                eq(2L), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsOwner(2L, "FUTURE");

        assertEquals(1, result.size());
        verify(bookingRepository).findByItemOwnerIdAndStartGreaterThanAndStatusInOrderByStartDesc(
                eq(2L), any(LocalDateTime.class), any());
    }

    @Test
    void getBookingsOwner_waitingState_success() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(2L, BookingStatus.WAITING))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsOwner(2L, "WAITING");

        assertEquals(1, result.size());
        verify(bookingRepository).findByItemOwnerIdAndStatusOrderByStartDesc(2L, BookingStatus.WAITING);
    }

    @Test
    void getBookingsOwner_rejectedState_success() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusInOrderByStartDesc(eq(2L), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        Collection<BookingDto> result = service.getBookingsOwner(2L, "REJECTED");

        assertEquals(1, result.size());
        verify(bookingRepository).findByItemOwnerIdAndStatusInOrderByStartDesc(eq(2L), any());
    }

    @Test
    void addNewBooking_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsOverlappingBookings(eq(10L), any(), any(), eq(BookingStatus.APPROVED)))
                .thenReturn(false);
        when(bookingMapper.mapToBooking(newBookingRequest, booker, item)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = service.addNewBooking(newBookingRequest, 1L);

        assertEquals(100L, result.getId());
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(10L);
        verify(bookingRepository).existsOverlappingBookings(eq(10L), any(), any(), eq(BookingStatus.APPROVED));
        verify(bookingMapper).mapToBooking(newBookingRequest, booker, item);
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBookingStatus_approved_success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = service.updateBookingStatus(100L, 2L, true);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        assertEquals(100L, result.getId());
        verify(bookingRepository).findById(100L);
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBookingStatus_rejected_success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = service.updateBookingStatus(100L, 2L, false);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
        assertEquals(100L, result.getId());
        verify(bookingRepository).findById(100L);
        verify(bookingRepository).save(booking);
    }

    @Test
    void getBookingById_success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = service.getBookingById(100L, 1L);

        assertEquals(100L, result.getId());
        verify(bookingRepository).findById(100L);
        verify(bookingMapper).mapToBookingDto(booking);
    }

    @Test
    void deleteBooking_success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        service.deleteBooking(100L, 1L);

        verify(bookingRepository).delete(booking);
    }
}
