package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class UpdateUserDtoJsonTest {

    @Autowired
    private JacksonTester<UpdateUserDto> json;

    @Test
    void shouldSerializeUpdateUserDto() throws Exception {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        JsonContent<UpdateUserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john@example.com");
    }

    @Test
    void shouldDeserializeUpdateUserDto() throws Exception {
        String content = "{\"id\":5,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getName()).isEqualTo("Jane Smith");
        assertThat(dto.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void shouldHandleNullId() throws Exception {
        String content = "{\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldHandleNullName() throws Exception {
        String content = "{\"id\":1,\"name\":null,\"email\":\"test@example.com\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldHandleNullEmail() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test User\",\"email\":null}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldHandleMissingFields() throws Exception {
        String content = "{}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldHandleNullValuesInJson() throws Exception {
        String content = "{\"id\":null,\"name\":null,\"email\":null}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldIgnoreUnknownProperties() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\",\"unknownField\":\"value\",\"age\":30}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldSerializeWithNullFields() throws Exception {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        JsonContent<UpdateUserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.id").isNull();
        assertThat(result).extractingJsonPathStringValue("$.name").isNull();
        assertThat(result).extractingJsonPathStringValue("$.email").isNull();
    }

    @Test
    void shouldHandleOnlyIdField() throws Exception {
        String content = "{\"id\":10}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldHandleOnlyNameField() throws Exception {
        String content = "{\"name\":\"Only Name\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isEqualTo("Only Name");
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldHandleOnlyEmailField() throws Exception {
        String content = "{\"email\":\"only@example.com\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isEqualTo("only@example.com");
    }

    @Test
    void shouldHandleNameAndEmailOnly() throws Exception {
        String content = "{\"name\":\"Name Only\",\"email\":\"email@example.com\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isEqualTo("Name Only");
        assertThat(dto.getEmail()).isEqualTo("email@example.com");
    }

    @Test
    void shouldHandleIdAndNameOnly() throws Exception {
        String content = "{\"id\":7,\"name\":\"Name Only\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getName()).isEqualTo("Name Only");
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void shouldHandleIdAndEmailOnly() throws Exception {
        String content = "{\"id\":8,\"email\":\"email@example.com\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(8L);
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isEqualTo("email@example.com");
    }

    @Test
    void shouldHandleEmptyName() throws Exception {
        String content = "{\"id\":1,\"name\":\"\",\"email\":\"test@example.com\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEmpty();
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldHandleEmptyEmail() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test User\",\"email\":\"\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEmpty();
    }

    @Test
    void shouldHandleComplexEmail() throws Exception {
        String content = "{\"id\":1,\"name\":\"User\",\"email\":\"user.name+tag@sub.domain.co.uk\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("User");
        assertThat(dto.getEmail()).isEqualTo("user.name+tag@sub.domain.co.uk");
    }

    @Test
    void shouldHandleUnicodeInName() throws Exception {
        String content = "{\"id\":1,\"name\":\"Иван Петров\",\"email\":\"ivan@example.com\"}";

        UpdateUserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Иван Петров");
        assertThat(dto.getEmail()).isEqualTo("ivan@example.com");
    }
}
