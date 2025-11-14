package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.NewItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class NewItemDtoJsonTest {

    @Autowired
    private JacksonTester<NewItemDto> json;

    @Test
    void shouldSerializeNewItemDto() throws Exception {
        NewItemDto dto = NewItemDto.builder()
                .name("Дрель")
                .description("Мощная дрель для дома")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<NewItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Мощная дрель для дома");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void shouldDeserializeNewItemDto() throws Exception {
        String content = "{\"name\":\"Молоток\",\"description\":\"Простой молоток\",\"available\":true,\"requestId\":5}";

        NewItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Молоток");
        assertThat(dto.getDescription()).isEqualTo("Простой молоток");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(5L);
    }

    @Test
    void shouldHandleNullRequestId() throws Exception {
        String content = "{\"name\":\"Вещь\",\"description\":\"Описание\",\"available\":true}";

        NewItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Вещь");
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void shouldHandleNullAvailable() throws Exception {
        String content = "{\"name\":\"Вещь\",\"description\":\"Описание\",\"requestId\":1}";

        NewItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Вещь");
        assertThat(dto.getAvailable()).isNull();
    }

    @Test
    void shouldIgnoreUnknownProperties() throws Exception {
        String content = "{\"name\":\"Тест\",\"description\":\"Описание\",\"available\":true,\"unknownField\":\"value\"}";

        NewItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Тест");
        assertThat(dto.getDescription()).isEqualTo("Описание");
        assertThat(dto.getAvailable()).isTrue();
    }

    @Test
    void shouldHandleEmptyObject() throws Exception {
        String content = "{}";

        NewItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void shouldHandleNullValuesInJson() throws Exception {
        String content = "{\"name\":null,\"description\":null,\"available\":null,\"requestId\":null}";

        NewItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void shouldSerializeWithNullFields() throws Exception {
        NewItemDto dto = NewItemDto.builder()
                .name(null)
                .description(null)
                .available(null)
                .requestId(null)
                .build();

        JsonContent<NewItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isNull();
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
        assertThat(result).extractingJsonPathStringValue("$.available").isNull();
        assertThat(result).extractingJsonPathStringValue("$.requestId").isNull();
    }
}
