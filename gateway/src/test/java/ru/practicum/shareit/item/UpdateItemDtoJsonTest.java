package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class UpdateItemDtoJsonTest {

    @Autowired
    private JacksonTester<UpdateItemDto> json;

    @Test
    void shouldSerializePartialUpdate() throws Exception {
        UpdateItemDto dto = UpdateItemDto.builder()
                .name("Новое название")
                .available(true)
                .build(); // description = null

        JsonContent<UpdateItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Новое название");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
    }

    @Test
    void shouldDeserializePartialUpdate() throws Exception {
        String content = "{\"name\":\"Обновленное название\",\"available\":false}";

        UpdateItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Обновленное название");
        assertThat(dto.getAvailable()).isFalse();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getOwner()).isNull();
        assertThat(dto.isAtLeastOneFieldPresent()).isTrue();
    }

    @Test
    void shouldDeserializeSingleFieldUpdate() throws Exception {
        String content = "{\"description\":\"Новое описание товара\"}";

        UpdateItemDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isEqualTo("Новое описание товара");
        assertThat(dto.getName()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getOwner()).isNull();
        assertThat(dto.isAtLeastOneFieldPresent()).isTrue();
    }

    @Test
    void shouldHandleNullIdAndOwnerInJson() throws Exception {
        String content = "{\"name\":\"Тест\",\"id\":null,\"owner\":null}";

        UpdateItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Тест");
        assertThat(dto.getId()).isNull();
        assertThat(dto.getOwner()).isNull();
    }

    @Test
    void shouldIgnoreIdAndOwnerFields() throws Exception {
        String content = "{\"name\":\"Тест\",\"id\":123,\"owner\":456}";

        UpdateItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Тест");
        assertThat(dto.getId()).isEqualTo(123L);
        assertThat(dto.getOwner()).isEqualTo(456L);
    }

    @Test
    void shouldHandleEmptyObject() throws Exception {
        String content = "{}";

        UpdateItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getOwner()).isNull();
        assertThat(dto.isAtLeastOneFieldPresent()).isFalse();
    }

    @Test
    void shouldHandleNullValuesInJson() throws Exception {
        String content = "{\"name\":null,\"description\":null,\"available\":null,\"id\":null,\"owner\":null}";

        UpdateItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getOwner()).isNull();
        assertThat(dto.isAtLeastOneFieldPresent()).isFalse();
    }

    @Test
    void shouldIgnoreUnknownProperties() throws Exception {
        String content = "{\"name\":\"Тест\",\"unknownField\":\"value\",\"anotherUnknown\":123}";

        UpdateItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Тест");
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
    }

    @Test
    void shouldHandleOnlyAvailableField() throws Exception {
        String content = "{\"available\":true}";

        UpdateItemDto dto = json.parseObject(content);

        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.isAtLeastOneFieldPresent()).isTrue();
    }

    @Test
    void shouldHandleOnlyNameField() throws Exception {
        String content = "{\"name\":\"Только название\"}";

        UpdateItemDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Только название");
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.isAtLeastOneFieldPresent()).isTrue();
    }

    @Test
    void shouldHandleOnlyDescriptionField() throws Exception {
        String content = "{\"description\":\"Только описание\"}";

        UpdateItemDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isEqualTo("Только описание");
        assertThat(dto.getName()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.isAtLeastOneFieldPresent()).isTrue();
    }
}
