package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void mapToUserDto_shouldMapAllFields() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        UserDto result = userMapper.mapToUserDto(user);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void mapToUserDto_shouldHandleNull() {
        UserDto result = userMapper.mapToUserDto(null);

        assertThat(result).isNull();
    }

    @Test
    void mapToUser_shouldMapFieldsAndIgnoreId() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Jane Smith");
        newUserDto.setEmail("jane@example.com");

        User result = userMapper.mapToUser(newUserDto);

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Jane Smith");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void mapToUser_shouldHandleNull() {
        User result = userMapper.mapToUser(null);

        assertThat(result).isNull();
    }

    @Test
    void updateUserFields_shouldUpdateNameAndEmail() {
        User user = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .build();

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("New Name");
        updateUserDto.setEmail("new@example.com");

        User result = userMapper.updateUserFields(updateUserDto, user);

        assertThat(result).isSameAs(user);
        assertThat(user.getName()).isEqualTo("New Name");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    void updateUserFields_shouldUpdateOnlyName() {
        User user = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .build();

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("New Name");
        // email не устанавливаем

        User result = userMapper.updateUserFields(updateUserDto, user);

        assertThat(result).isSameAs(user);
        assertThat(user.getName()).isEqualTo("New Name");
        assertThat(user.getEmail()).isEqualTo("old@example.com"); // остался старый
        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    void updateUserFields_shouldUpdateOnlyEmail() {
        User user = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .build();

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail("new@example.com");
        // name не устанавливаем

        User result = userMapper.updateUserFields(updateUserDto, user);

        assertThat(result).isSameAs(user);
        assertThat(user.getName()).isEqualTo("Old Name"); // осталось старое
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    void updateUserFields_shouldTrimNameAndEmail() {
        User user = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .build();

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("  New Name  ");
        updateUserDto.setEmail("  new@example.com  ");

        User result = userMapper.updateUserFields(updateUserDto, user);

        assertThat(result).isSameAs(user);
        assertThat(user.getName()).isEqualTo("New Name"); // пробелы обрезаны
        assertThat(user.getEmail()).isEqualTo("new@example.com"); // пробелы обрезаны
    }

    @Test
    void updateUserFields_shouldNotUpdateWhenNoChanges() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        UpdateUserDto updateUserDto = new UpdateUserDto();
        // не устанавливаем ни name, ни email

        User result = userMapper.updateUserFields(updateUserDto, user);

        assertThat(result).isSameAs(user);
        assertThat(user.getName()).isEqualTo("John Doe"); // не изменилось
        assertThat(user.getEmail()).isEqualTo("john@example.com"); // не изменилось
    }

    @Test
    void updateUserFields_shouldHandleNullUser() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("New Name");

        assertThatThrownBy(() -> userMapper.updateUserFields(updateUserDto, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void updateUserFields_shouldHandleEmptyName() {
        User user = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .build();

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName(""); // пустое имя

        User result = userMapper.updateUserFields(updateUserDto, user);

        assertThat(result).isSameAs(user);
        assertThat(user.getName()).isEqualTo("Old Name");
        assertThat(user.getEmail()).isEqualTo("old@example.com");
    }

    @Test
    void updateUserFields_shouldHandleNullName() {
        User user = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .build();

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName(null); // null имя

        User result = userMapper.updateUserFields(updateUserDto, user);

        assertThat(result).isSameAs(user);
        assertThat(user.getName()).isEqualTo("Old Name"); // не изменилось
        assertThat(user.getEmail()).isEqualTo("old@example.com"); // не изменилось
    }
}
