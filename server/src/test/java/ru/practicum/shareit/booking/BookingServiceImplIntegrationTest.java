package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ShareItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final BookingService bookingService;

    private User owner;
    private User booker;
    private User anotherUser;
    private Item availableItem;
    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;
    private Booking waitingBooking;
    private Booking rejectedBooking;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();

        owner = userRepository.save(User.builder()
                .name("Owner User")
                .email("owner@example.com")
                .build());

        booker = userRepository.save(User.builder()
                .name("Booker User")
                .email("booker@example.com")
                .build());

        anotherUser = userRepository.save(User.builder()
                .name("Another User")
                .email("another@example.com")
                .build());

        availableItem = itemRepository.save(Item.builder()
                .owner(owner)
                .name("Available Item")
                .description("Item available for booking")
                .available(true)
                .build());

        pastBooking = bookingRepository.save(Booking.builder()
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .build());

        currentBooking = bookingRepository.save(Booking.builder()
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().plusHours(2))
                .build());

        futureBooking = bookingRepository.save(Booking.builder()
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build());

        waitingBooking = bookingRepository.save(Booking.builder()
                .item(availableItem)
                .booker(anotherUser)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build());

        rejectedBooking = bookingRepository.save(Booking.builder()
                .item(availableItem)
                .booker(anotherUser)
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(6))
                .build());
    }

    @Test
    void getBookingsUser_allState_success() {
        Collection<BookingDto> result = bookingService.getBookingsUser(booker.getId(), "ALL");

        assertThat(result).hasSize(3);
        assertThat(result).extracting(BookingDto::getId)
                .containsExactlyInAnyOrder(pastBooking.getId(), currentBooking.getId(), futureBooking.getId());
    }

    @Test
    void getBookingsUser_currentState_success() {
        Collection<BookingDto> result = bookingService.getBookingsUser(booker.getId(), "CURRENT");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void getBookingsUser_pastState_success() {
        Collection<BookingDto> result = bookingService.getBookingsUser(booker.getId(), "PAST");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void getBookingsUser_futureState_success() {
        Collection<BookingDto> result = bookingService.getBookingsUser(booker.getId(), "FUTURE");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(futureBooking.getId());
    }

    @Test
    void getBookingsUser_waitingState_success() {
        Collection<BookingDto> result = bookingService.getBookingsUser(anotherUser.getId(), "WAITING");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(waitingBooking.getId());
    }

    @Test
    void getBookingsUser_rejectedState_success() {
        Collection<BookingDto> result = bookingService.getBookingsUser(anotherUser.getId(), "REJECTED");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(rejectedBooking.getId());
    }

    @Test
    void getBookingsOwner_allState_success() {
        Collection<BookingDto> result = bookingService.getBookingsOwner(owner.getId(), "ALL");

        assertThat(result).hasSize(5);
    }

    @Test
    void getBookingsOwner_currentState_success() {
        Collection<BookingDto> result = bookingService.getBookingsOwner(owner.getId(), "CURRENT");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void getBookingsOwner_pastState_success() {
        Collection<BookingDto> result = bookingService.getBookingsOwner(owner.getId(), "PAST");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void getBookingsOwner_futureState_success() {
        Collection<BookingDto> result = bookingService.getBookingsOwner(owner.getId(), "FUTURE");

        assertThat(result).hasSize(2);
    }

    @Test
    void getBookingsOwner_waitingState_success() {
        Collection<BookingDto> result = bookingService.getBookingsOwner(owner.getId(), "WAITING");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(waitingBooking.getId());
    }

    @Test
    void getBookingsOwner_rejectedState_success() {
        Collection<BookingDto> result = bookingService.getBookingsOwner(owner.getId(), "REJECTED");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(rejectedBooking.getId());
    }

    @Test
    void addNewBooking_success() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(availableItem.getId());
        request.setStart(LocalDateTime.now().plusDays(10));
        request.setEnd(LocalDateTime.now().plusDays(11));

        BookingDto result = bookingService.addNewBooking(request, booker.getId());

        assertNotNull(result);
        assertThat(result.getItem().getId()).isEqualTo(availableItem.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void updateBookingStatus_approved_success() {
        BookingDto result = bookingService.updateBookingStatus(waitingBooking.getId(), owner.getId(), true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);

        Booking updatedBooking = bookingRepository.findById(waitingBooking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void updateBookingStatus_rejected_success() {
        BookingDto result = bookingService.updateBookingStatus(waitingBooking.getId(), owner.getId(), false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.findById(waitingBooking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void getBookingById_successForBooker() {
        BookingDto result = bookingService.getBookingById(pastBooking.getId(), booker.getId());

        assertThat(result.getId()).isEqualTo(pastBooking.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void getBookingById_successForOwner() {
        BookingDto result = bookingService.getBookingById(pastBooking.getId(), owner.getId());

        assertThat(result.getId()).isEqualTo(pastBooking.getId());
        assertThat(result.getItem().getId()).isEqualTo(availableItem.getId());
    }

    @Test
    void deleteBooking_success() {
        bookingService.deleteBooking(waitingBooking.getId(), anotherUser.getId());

        assertThat(bookingRepository.findById(waitingBooking.getId())).isEmpty();
    }

    @Test
    void getBookingsUser_emptyResults() {
        User newUser = userRepository.save(User.builder()
                .name("New User")
                .email("newuser@example.com")
                .build());

        Collection<BookingDto> result = bookingService.getBookingsUser(newUser.getId(), "ALL");

        assertThat(result).isEmpty();
    }

    @Test
    void getBookingsOwner_emptyResults() {
        User userWithoutItems = userRepository.save(User.builder()
                .name("No Items User")
                .email("noitems@example.com")
                .build());

        Collection<BookingDto> result = bookingService.getBookingsOwner(userWithoutItems.getId(), "ALL");

        assertThat(result).isEmpty();
    }

    @Test
    void getBookingsUser_multipleBookings_success() {
        Booking additionalBooking = bookingRepository.save(Booking.builder()
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(7))
                .end(LocalDateTime.now().plusDays(8))
                .build());

        Collection<BookingDto> result = bookingService.getBookingsUser(booker.getId(), "ALL");

        assertThat(result).hasSize(4);
    }

    @Test
    void getBookingsOwner_multipleBookings_success() {
        Booking additionalBooking = bookingRepository.save(Booking.builder()
                .item(availableItem)
                .booker(anotherUser)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(7))
                .end(LocalDateTime.now().plusDays(8))
                .build());

        Collection<BookingDto> result = bookingService.getBookingsOwner(owner.getId(), "ALL");

        assertThat(result).hasSize(6);
    }
}