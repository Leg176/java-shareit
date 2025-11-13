package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User booker1;
    private User booker2;
    private User owner1;
    private User owner2;
    private Item item1;
    private Item item2;
    private Item item3;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Booking booking5;

    @BeforeEach
    void setUp() {
        booker1 = userRepository.save(User.builder()
                .name("booker1")
                .email("booker1@mail.com")
                .build());

        booker2 = userRepository.save(User.builder()
                .name("booker2")
                .email("booker2@mail.com")
                .build());

        owner1 = userRepository.save(User.builder()
                .name("owner1")
                .email("owner1@mail.com")
                .build());

        owner2 = userRepository.save(User.builder()
                .name("owner2")
                .email("owner2@mail.com")
                .build());

        item1 = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner1)
                .build());

        item2 = itemRepository.save(Item.builder()
                .name("Hammer")
                .description("Steel hammer")
                .available(true)
                .owner(owner1)
                .build());

        item3 = itemRepository.save(Item.builder()
                .name("Saw")
                .description("Wood saw")
                .available(true)
                .owner(owner2)
                .build());

        // Прошлое бронирование (завершено)
        booking1 = bookingRepository.save(Booking.builder()
                .item(item1)
                .booker(booker1)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build());

        // Текущее бронирование (идет сейчас)
        booking2 = bookingRepository.save(Booking.builder()
                .item(item1)
                .booker(booker2)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .build());

        // Будущее бронирование (еще не началось)
        booking3 = bookingRepository.save(Booking.builder()
                .item(item2)
                .booker(booker1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        // Отклоненное бронирование
        booking4 = bookingRepository.save(Booking.builder()
                .item(item3)
                .booker(booker2)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.REJECTED)
                .build());

        booking5 = bookingRepository.save(Booking.builder()
                .item(item1)
                .booker(booker1)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build());
    }

    @Test
    void findByBookerIdOrderByStartDesc_returnsBookerBookings() {
        Collection<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(booker1.getId());

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(booking -> booking.getBooker().getId().equals(booker1.getId())));
    }

    @Test
    void findByBookerIdOrderByStartDesc_whenNoBookings_returnsEmpty() {
        Collection<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualAndStatusIn_returnsCurrentBookings() {
        LocalDateTime now = LocalDateTime.now();
        Collection<BookingStatus> statuses = List.of(BookingStatus.APPROVED);

        Collection<Booking> result = bookingRepository.
                findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualAndStatusInOrderByStartDesc(
                booker2.getId(), now, now, statuses);

        assertEquals(1, result.size());
        assertEquals(booking2.getId(), result.iterator().next().getId());
    }

    @Test
    void findByBookerIdAndStartGreaterThanAndStatusIn_returnsFutureBookings() {
        LocalDateTime now = LocalDateTime.now();
        Collection<BookingStatus> statuses = List.of(BookingStatus.WAITING, BookingStatus.APPROVED);

        Collection<Booking> result = bookingRepository.findByBookerIdAndStartGreaterThanAndStatusInOrderByStartDesc(
                booker1.getId(), now, statuses);

        assertEquals(1, result.size());
        assertEquals(booking3.getId(), result.iterator().next().getId());
    }

    @Test
    void findByBookerIdAndEndLessThanAndStatus_returnsPastBookings() {
        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> result = bookingRepository.findByBookerIdAndEndLessThanAndStatusOrderByStartDesc(
                booker1.getId(), now, BookingStatus.APPROVED);

        List<Long> foundBookingIds = result.stream()
                .map(Booking::getId)
                .collect(Collectors.toList());

        assertTrue(foundBookingIds.contains(booking1.getId()));
        assertTrue(foundBookingIds.contains(booking5.getId()));
        assertEquals(2, result.size());
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc_returnsBookingsByStatus() {
        Collection<Booking> result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                booker1.getId(), BookingStatus.WAITING);

        assertEquals(1, result.size());
        assertEquals(booking3.getId(), result.iterator().next().getId());
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc_returnsOwnerBookings() {
        Collection<Booking> result = bookingRepository.findByItemOwnerIdOrderByStartDesc(owner1.getId());

        assertEquals(4, result.size());
        assertTrue(result.stream().allMatch(booking -> booking.getItem().getOwner().getId().
                equals(owner1.getId())));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDesc_returnsOwnerBookingsByStatus() {
        Collection<Booking> result = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                owner1.getId(), BookingStatus.WAITING);

        assertEquals(1, result.size());
        assertEquals(booking3.getId(), result.iterator().next().getId());
    }

    @Test
    void findTopByItemIdAndStatusAndEndLessThanOrderByEndDesc_returnsLastCompletedBooking() {
        LocalDateTime now = LocalDateTime.now();
        Optional<Booking> result = bookingRepository.findTopByItemIdAndStatusAndEndLessThanOrderByEndDesc(
                item1.getId(), BookingStatus.APPROVED, now);

        assertTrue(result.isPresent());

        Booking foundBooking = result.get();
        assertEquals(item1.getId(), foundBooking.getItem().getId());
        assertEquals(BookingStatus.APPROVED, foundBooking.getStatus());
        assertTrue(foundBooking.getEnd().isBefore(now));

        List<Booking> allCompletedBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getItem().getId().equals(item1.getId()))
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .filter(b -> b.getEnd().isBefore(now))
                .sorted((b1, b2) -> b2.getEnd().compareTo(b1.getEnd()))
                .collect(Collectors.toList());

        if (!allCompletedBookings.isEmpty()) {
            Booking expectedLastBooking = allCompletedBookings.get(0);
            assertEquals(expectedLastBooking.getId(), foundBooking.getId());
        }
    }

    @Test
    void findTopByItemIdAndStatusAndStartGreaterThanOrderByStartAsc_returnsNextBooking() {
        LocalDateTime now = LocalDateTime.now();
        Optional<Booking> result = bookingRepository.findTopByItemIdAndStatusAndStartGreaterThanOrderByStartAsc(
                item2.getId(), BookingStatus.WAITING, now);

        assertTrue(result.isPresent());
        assertEquals(booking3.getId(), result.get().getId());
    }

    @Test
    void existsOverlappingBookings_returnsTrueWhenOverlap() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        boolean result = bookingRepository.existsOverlappingBookings(item1.getId(), start, end, BookingStatus.APPROVED);

        assertTrue(result);
    }

    @Test
    void existsOverlappingBookings_returnsFalseWhenNoOverlap() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        boolean result = bookingRepository.existsOverlappingBookings(item1.getId(), start, end, BookingStatus.APPROVED);

        assertFalse(result);
    }

    @Test
    void save_findAll_findById_basicOperations() {
        List<Booking> all = bookingRepository.findAll();
        Optional<Booking> byId = bookingRepository.findById(booking1.getId());

        assertEquals(5, all.size());
        assertTrue(byId.isPresent());
        assertEquals(item1.getId(), byId.get().getItem().getId());
        assertEquals(booker1.getId(), byId.get().getBooker().getId());
        assertEquals(BookingStatus.APPROVED, byId.get().getStatus());
    }

    @Test
    void delete_removesBooking() {
        bookingRepository.delete(booking1);

        Optional<Booking> result = bookingRepository.findById(booking1.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByBookerIdAndStatusIn_returnsBookingsByMultipleStatuses() {
        Collection<BookingStatus> statuses = List.of(BookingStatus.WAITING, BookingStatus.REJECTED);
        Collection<Booking> result = bookingRepository.
                findByBookerIdAndStatusInOrderByStartDesc(booker2.getId(), statuses);

        assertEquals(1, result.size());
        assertEquals(booking4.getId(), result.iterator().next().getId());
    }
    @Test
    void existsByBookerIdAndItemIdAndEndBefore_returnsTrueWhenExists() {
        boolean result = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                booker1.getId(), item1.getId(), BookingStatus.APPROVED);

        assertTrue(result);
    }

    @Test
    void existsByBookerIdAndItemIdAndEndBefore_returnsFalseWhenNotExists() {
        boolean result = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                booker2.getId(), item1.getId(), BookingStatus.APPROVED);

        assertFalse(result);
    }

}
