package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.NewCommentRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class NewCommentRequestJsonTest {

    @Autowired
    private JacksonTester<NewCommentRequest> json;

    @Test
    void shouldSerializeNewCommentRequest() throws Exception {
        NewCommentRequest commentRequest = NewCommentRequest.builder()
                .text("Отличная вещь! Очень доволен использованием.")
                .build();

        JsonContent<NewCommentRequest> result = json.write(commentRequest);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Отличная вещь! Очень доволен использованием.");
    }

    @Test
    void shouldDeserializeNewCommentRequest() throws Exception {
        String content = "{\"text\":\"Очень полезная вещь, рекомендую!\"}";

        NewCommentRequest commentRequest = json.parseObject(content);

        assertThat(commentRequest.getText()).isEqualTo("Очень полезная вещь, рекомендую!");
    }

    @Test
    void shouldHandleNullText() throws Exception {
        String content = "{\"text\":null}";

        NewCommentRequest commentRequest = json.parseObject(content);

        assertThat(commentRequest.getText()).isNull();
    }

    @Test
    void shouldHandleMissingText() throws Exception {
        String content = "{}";

        NewCommentRequest commentRequest = json.parseObject(content);

        assertThat(commentRequest.getText()).isNull();
    }

    @Test
    void shouldHandleLongText() throws Exception {
        String longText = "О".repeat(1000) + "чень длинный комментарий с подробным описанием";
        NewCommentRequest commentRequest = NewCommentRequest.builder()
                .text(longText)
                .build();

        JsonContent<NewCommentRequest> result = json.write(commentRequest);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(longText);
    }

    @Test
    void shouldHandleTextWithUnicode() throws Exception {
        String unicodeText = "Комментарий с эмодзи: 😊 и кириллицей: Привет!";
        NewCommentRequest commentRequest = NewCommentRequest.builder()
                .text(unicodeText)
                .build();

        JsonContent<NewCommentRequest> result = json.write(commentRequest);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(unicodeText);
    }

    @Test
    void shouldIgnoreUnknownProperties() throws Exception {
        String content = "{\"text\":\"Обычный комментарий\",\"unknownField\":\"someValue\",\"anotherField\":123}";

        NewCommentRequest commentRequest = json.parseObject(content);

        assertThat(commentRequest.getText()).isEqualTo("Обычный комментарий");
    }

    @Test
    void shouldSerializeWithNullText() throws Exception {
        NewCommentRequest commentRequest = NewCommentRequest.builder()
                .text(null)
                .build();

        JsonContent<NewCommentRequest> result = json.write(commentRequest);

        assertThat(result).extractingJsonPathStringValue("$.text").isNull();
    }

    @Test
    void shouldHandleMultilineText() throws Exception {
        String multilineText = "Первый абзац.\nВторой абзац.\nТретий абзац.";
        NewCommentRequest commentRequest = NewCommentRequest.builder()
                .text(multilineText)
                .build();

        JsonContent<NewCommentRequest> result = json.write(commentRequest);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(multilineText);
    }
}
