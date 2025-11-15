package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import java.time.LocalDateTime;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.HttpHeaders.X_SHARER_USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto bookingDto1;
    private BookingDto bookingDto2;
    private NewBookingRequest newBookingRequest;

    @BeforeEach
    void setUp() {
        bookingDto1 = BookingDto.builder()
                .id(1L)
                .item(BookingItemDto.builder().id(10L).name("Drill").build())
                .booker(BookingBookerDto.builder().id(1L).build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        bookingDto2 = BookingDto.builder()
                .id(2L)
                .item(BookingItemDto.builder().id(20L).name("Hammer").build())
                .booker(BookingBookerDto.builder().id(1L).build())
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.APPROVED)
                .build();

        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setItemId(10L);
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void findBookingsUser_success() throws Exception {
        given(bookingService.getBookingsUser(1L, "ALL")).willReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }

    @Test
    void findAllBookingForOwner_success() throws Exception {
        given(bookingService.getBookingsOwner(2L, "ALL")).willReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, 2L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getBooking_success() throws Exception {
        given(bookingService.getBookingById(1L, 1L)).willReturn(bookingDto1);

        mockMvc.perform(get("/bookings/1")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(10))
                .andExpect(jsonPath("$.item.name").value("Drill"));
    }

    @Test
    void create_success() throws Exception {
        given(bookingService.addNewBooking(any(NewBookingRequest.class), eq(1L))).willReturn(bookingDto1);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(10));
    }

    @Test
    void updateBookingStatus_approved_success() throws Exception {
        BookingDto approvedBooking = BookingDto.builder()
                .id(1L)
                .item(BookingItemDto.builder().id(10L).name("Drill").build())
                .booker(BookingBookerDto.builder().id(1L).build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();

        given(bookingService.updateBookingStatus(1L, 2L, true)).willReturn(approvedBooking);

        mockMvc.perform(patch("/bookings/1")
                        .header(X_SHARER_USER_ID, 2L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void updateBookingStatus_rejected_success() throws Exception {
        BookingDto rejectedBooking = BookingDto.builder()
                .id(1L)
                .item(BookingItemDto.builder().id(10L).name("Drill").build())
                .booker(BookingBookerDto.builder().id(1L).build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.REJECTED)
                .build();

        given(bookingService.updateBookingStatus(1L, 2L, false)).willReturn(rejectedBooking);

        mockMvc.perform(patch("/bookings/1")
                        .header(X_SHARER_USER_ID, 2L)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void deleteBooking_success() throws Exception {
        doNothing().when(bookingService).deleteBooking(1L, 1L);

        mockMvc.perform(delete("/bookings/1")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk());

        verify(bookingService).deleteBooking(1L, 1L);
    }

    @Test
    void findBookingsUser_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllBookingForOwner_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooking_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookingStatus_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBooking_withoutUserId_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/bookings/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findBookingsUser_withoutState_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllBookingForOwner_withoutState_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, 2L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookingStatus_withoutApproved_returnsBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .header(X_SHARER_USER_ID, 2L))
                .andExpect(status().isBadRequest());
    }
}
