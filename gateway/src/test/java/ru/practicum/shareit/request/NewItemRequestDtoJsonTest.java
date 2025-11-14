package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class NewItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<NewItemRequestDto> json;

    @Test
    void shouldSerializeNewItemRequestDto() throws Exception {
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description("Отличная вещь для дома")
                .build();

        JsonContent<NewItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Отличная вещь для дома");
    }

    @Test
    void shouldDeserializeNewItemRequestDto() throws Exception {
        String content = "{\"description\":\"Полезный инструмент для работы\"}";

        NewItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isEqualTo("Полезный инструмент для работы");
    }

    @Test
    void shouldHandleNullDescription() throws Exception {
        String content = "{\"description\":null}";

        NewItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void shouldHandleMissingDescription() throws Exception {
        String content = "{}";

        NewItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void shouldHandleLongDescription() throws Exception {
        String longDescription = "D".repeat(500) + " очень длинное описание";
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description(longDescription)
                .build();

        JsonContent<NewItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(longDescription);
    }

    @Test
    void shouldHandleDescriptionWithSpecialCharacters() throws Exception {
        String descriptionWithSpecialChars = "Описание со спецсимволами: !@#$%^&*()_+-=[]{}|;:',.<>?/\"";
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description(descriptionWithSpecialChars)
                .build();

        JsonContent<NewItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(descriptionWithSpecialChars);
    }

    @Test
    void shouldHandleDescriptionWithUnicode() throws Exception {
        String unicodeDescription = "Описание с эмодзи: 😊 и разными символами: Привет!";
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description(unicodeDescription)
                .build();

        JsonContent<NewItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(unicodeDescription);
    }

    @Test
    void shouldIgnoreUnknownProperties() throws Exception {
        String content = "{\"description\":\"Обычное описание\",\"unknownField\":\"someValue\",\"anotherField\":123}";

        NewItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isEqualTo("Обычное описание");
    }

    @Test
    void shouldSerializeWithNullDescription() throws Exception {
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description(null)
                .build();

        JsonContent<NewItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
    }

    @Test
    void shouldHandleMultilineDescription() throws Exception {
        String multilineDescription = "Первая строка описания.\nВторая строка описания.\nТретья строка.";
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description(multilineDescription)
                .build();

        JsonContent<NewItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(multilineDescription);
    }

    @Test
    void shouldHandleEmptyDescription() throws Exception {
        String content = "{\"description\":\"\"}";

        NewItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isEmpty();
    }

    @Test
    void shouldHandleWhitespaceDescription() throws Exception {
        String content = "{\"description\":\"   \"}";

        NewItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isEqualTo("   ");
    }
}
