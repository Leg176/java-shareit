package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class NewBookingRequestJsonTest {

    @Autowired
    private JacksonTester<NewBookingRequest> json;

    @Test
    void shouldSerializeNewBookingRequest() throws Exception {
        NewBookingRequest bookingRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2024, 10, 1, 10, 0))
                .end(LocalDateTime.of(2024, 10, 2, 10, 0))
                .build();

        JsonContent<NewBookingRequest> result = json.write(bookingRequest);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-10-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-10-02T10:00:00");
    }

    @Test
    void shouldDeserializeNewBookingRequest() throws Exception {
        String content = "{\"itemId\":1,\"start\":\"2024-10-01T10:00:00\",\"end\":\"2024-10-02T10:00:00\"}";

        NewBookingRequest bookingRequest = json.parseObject(content);

        assertThat(bookingRequest.getItemId()).isEqualTo(1L);
        assertThat(bookingRequest.getStart()).isEqualTo(LocalDateTime.of(2024, 10, 1, 10, 0));
        assertThat(bookingRequest.getEnd()).isEqualTo(LocalDateTime.of(2024, 10, 2, 10, 0));
    }

    @Test
    void shouldHandleNullFields() throws Exception {
        String content = "{\"itemId\":1}";

        NewBookingRequest bookingRequest = json.parseObject(content);

        assertThat(bookingRequest.getItemId()).isEqualTo(1L);
        assertThat(bookingRequest.getStart()).isNull();
        assertThat(bookingRequest.getEnd()).isNull();
    }

    @Test
    void shouldHandleFutureDates() throws Exception {
        LocalDateTime futureStart = LocalDateTime.of(2024, 12, 1, 10, 0);
        LocalDateTime futureEnd = LocalDateTime.of(2024, 12, 2, 10, 0);

        NewBookingRequest bookingRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(futureStart)
                .end(futureEnd)
                .build();

        JsonContent<NewBookingRequest> result = json.write(bookingRequest);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-12-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-12-02T10:00:00");
    }

    @Test
    void shouldSerializeWithNullValues() throws Exception {
        NewBookingRequest bookingRequest = NewBookingRequest.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        JsonContent<NewBookingRequest> result = json.write(bookingRequest);

        assertThat(result).extractingJsonPathStringValue("$.itemId").isNull();
        assertThat(result).extractingJsonPathStringValue("$.start").isNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNull();
    }

    @Test
    void shouldHandleDifferentTimeFormats() throws Exception {
        String content = "{\"itemId\":1,\"start\":\"2024-10-01T10:00:00\",\"end\":\"2024-10-02T15:30:45\"}";

        NewBookingRequest bookingRequest = json.parseObject(content);

        assertThat(bookingRequest.getStart()).isEqualTo(LocalDateTime.of(2024, 10, 1, 10, 0, 0));
        assertThat(bookingRequest.getEnd()).isEqualTo(LocalDateTime.of(2024, 10, 2, 15, 30, 45));
    }
}
