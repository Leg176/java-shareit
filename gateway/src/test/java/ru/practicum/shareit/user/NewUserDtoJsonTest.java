package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.dto.NewUserDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class NewUserDtoJsonTest {

    @Autowired
    private JacksonTester<NewUserDto> json;

    @Test
    void shouldSerializeNewUserDto() throws Exception {
        NewUserDto dto = NewUserDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        JsonContent<NewUserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john@example.com");
    }

    @Test
    void shouldDeserializeNewUserDto() throws Exception {
        String content = "{\"name\":\"Jane Smith\",\"email\":\"jane@example.com\"}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Jane Smith");
        assertThat(dto.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void shouldHandleNullName() throws Exception {
        String content = "{\"name\":null,\"email\":\"test@example.com\"}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldHandleNullEmail() throws Exception {
        String content = "{\"name\":\"Test User\",\"email\":null}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldHandleMissingFields() throws Exception {
        String content = "{}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldHandleNullValuesInJson() throws Exception {
        String content = "{\"name\":null,\"email\":null}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldIgnoreUnknownProperties() throws Exception {
        String content = "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"unknownField\":\"value\",\"age\":30}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldSerializeWithNullFields() throws Exception {
        NewUserDto dto = NewUserDto.builder()
                .name(null)
                .email(null)
                .build();

        JsonContent<NewUserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isNull();
        assertThat(result).extractingJsonPathStringValue("$.email").isNull();
    }

    @Test
    void shouldHandleOnlyNameField() throws Exception {
        String content = "{\"name\":\"Only Name\"}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Only Name");
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldHandleOnlyEmailField() throws Exception {
        String content = "{\"email\":\"only@example.com\"}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isEqualTo("only@example.com");
    }

    @Test
    void shouldHandleEmptyName() throws Exception {
        String content = "{\"name\":\"\",\"email\":\"test@example.com\"}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEmpty();
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldHandleEmptyEmail() throws Exception {
        String content = "{\"name\":\"Test User\",\"email\":\"\"}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEmpty();
    }

    @Test
    void shouldHandleWhitespaceName() throws Exception {
        String content = "{\"name\":\"   \",\"email\":\"test@example.com\"}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("   ");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldHandleWhitespaceEmail() throws Exception {
        String content = "{\"name\":\"Test User\",\"email\":\"   \"}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("   ");
    }

    @Test
    void shouldHandleComplexEmail() throws Exception {
        String content = "{\"name\":\"User\",\"email\":\"user.name+tag@sub.domain.co.uk\"}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("User");
        assertThat(dto.getEmail()).isEqualTo("user.name+tag@sub.domain.co.uk");
    }

    @Test
    void shouldHandleUnicodeInName() throws Exception {
        String content = "{\"name\":\"Иван Петров\",\"email\":\"ivan@example.com\"}";

        NewUserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Иван Петров");
        assertThat(dto.getEmail()).isEqualTo("ivan@example.com");
    }
}
