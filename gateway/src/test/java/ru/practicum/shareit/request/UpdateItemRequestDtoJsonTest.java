package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class UpdateItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<UpdateItemRequestDto> json;

    @Test
    void shouldSerializeUpdateItemRequestDto() throws Exception {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(1L)
                .description("Обновленное описание вещи")
                .build();

        JsonContent<UpdateItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Обновленное описание вещи");
    }

    @Test
    void shouldDeserializeUpdateItemRequestDto() throws Exception {
        String content = "{\"id\":5,\"description\":\"Новое описание товара\"}";

        UpdateItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getDescription()).isEqualTo("Новое описание товара");
    }

    @Test
    void shouldHandleNullId() throws Exception {
        String content = "{\"description\":\"Описание без ID\"}";

        UpdateItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getDescription()).isEqualTo("Описание без ID");
    }

    @Test
    void shouldHandleNullDescription() throws Exception {
        String content = "{\"id\":1,\"description\":null}";

        UpdateItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void shouldHandleMissingFields() throws Exception {
        String content = "{}";

        UpdateItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void shouldHandleNullValuesInJson() throws Exception {
        String content = "{\"id\":null,\"description\":null}";

        UpdateItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void shouldIgnoreUnknownProperties() throws Exception {
        String content = "{\"id\":1,\"description\":\"Тест\",\"unknownField\":\"value\",\"anotherField\":123}";

        UpdateItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Тест");
    }

    @Test
    void shouldSerializeWithNullFields() throws Exception {
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(null)
                .description(null)
                .build();

        JsonContent<UpdateItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
    }

    @Test
    void shouldHandleOnlyIdField() throws Exception {
        String content = "{\"id\":10}";

        UpdateItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void shouldHandleOnlyDescriptionField() throws Exception {
        String content = "{\"description\":\"Только описание\"}";

        UpdateItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getDescription()).isEqualTo("Только описание");
    }

    @Test
    void shouldHandleLongDescription() throws Exception {
        String longDescription = "D".repeat(300) + " очень длинное описание";
        UpdateItemRequestDto dto = UpdateItemRequestDto.builder()
                .id(1L)
                .description(longDescription)
                .build();

        JsonContent<UpdateItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(longDescription);
    }

    @Test
    void shouldHandleEmptyDescription() throws Exception {
        String content = "{\"id\":1,\"description\":\"\"}";

        UpdateItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEmpty();
    }

    @Test
    void shouldHandleWhitespaceDescription() throws Exception {
        String content = "{\"id\":1,\"description\":\"   \"}";

        UpdateItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("   ");
    }
}
