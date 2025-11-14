package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    private User existingUser1;
    private User existingUser2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        existingUser1 = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        existingUser2 = userRepository.save(User.builder()
                .name("Jane Smith")
                .email("jane@example.com")
                .build());
    }

    @Test
    void getAllUsers_success() {
        List<UserDto> result = (List<UserDto>) userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserDto::getEmail)
                .containsExactlyInAnyOrder("john@example.com", "jane@example.com");
    }

    @Test
    void getAllUsers_emptyList() {
        userRepository.deleteAll();

        List<UserDto> result = (List<UserDto>) userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void saveUser_success() {
        NewUserDto request = NewUserDto.builder()
                .name("New User")
                .email("newuser@example.com")
                .build();

        UserDto result = userService.saveUser(request);

        assertNotNull(result);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New User");
        assertThat(result.getEmail()).isEqualTo("newuser@example.com");

        User savedUser = userRepository.findById(result.getId()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("New User");
        assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");
    }

    @Test
    void updateUser_success() {
        UpdateUserDto request = UpdateUserDto.builder()
                .id(existingUser1.getId())
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        UserDto result = userService.updateUser(request);

        assertThat(result.getId()).isEqualTo(existingUser1.getId());
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");

        User updatedUser = userRepository.findById(existingUser1.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void updateUser_onlyName_success() {
        UpdateUserDto request = UpdateUserDto.builder()
                .id(existingUser1.getId())
                .name("Only Name Updated")
                .build();

        UserDto result = userService.updateUser(request);

        assertThat(result.getId()).isEqualTo(existingUser1.getId());
        assertThat(result.getName()).isEqualTo("Only Name Updated");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void updateUser_onlyEmail_success() {
        UpdateUserDto request = UpdateUserDto.builder()
                .id(existingUser1.getId())
                .email("onlyemail@example.com")
                .build();

        UserDto result = userService.updateUser(request);

        assertThat(result.getId()).isEqualTo(existingUser1.getId());
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("onlyemail@example.com");
    }

    @Test
    void updateUser_noChanges_success() {
        UpdateUserDto request = UpdateUserDto.builder()
                .id(existingUser1.getId())
                .build();

        UserDto result = userService.updateUser(request);

        assertThat(result.getId()).isEqualTo(existingUser1.getId());
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void getUserById_success() {
        UserDto result = userService.getUserById(existingUser1.getId());

        assertThat(result.getId()).isEqualTo(existingUser1.getId());
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void deleteUser_success() {
        userService.deleteUser(existingUser1.getId());

        assertThat(userRepository.findById(existingUser1.getId())).isEmpty();
        assertThat(userRepository.findById(existingUser2.getId())).isPresent();
    }

    @Test
    void saveUser_multipleUsers_success() {
        NewUserDto request1 = NewUserDto.builder()
                .name("User One")
                .email("user1@example.com")
                .build();

        NewUserDto request2 = NewUserDto.builder()
                .name("User Two")
                .email("user2@example.com")
                .build();

        UserDto result1 = userService.saveUser(request1);
        UserDto result2 = userService.saveUser(request2);

        assertThat(result1.getEmail()).isEqualTo("user1@example.com");
        assertThat(result2.getEmail()).isEqualTo("user2@example.com");

        List<UserDto> allUsers = (List<UserDto>) userService.getAllUsers();
        assertThat(allUsers).hasSize(4);
    }

    @Test
    void saveUser_specialCharacters_success() {
        NewUserDto request = NewUserDto.builder()
                .name("Иван Петров")
                .email("иван@пример.рф")
                .build();

        UserDto result = userService.saveUser(request);

        assertThat(result.getName()).isEqualTo("Иван Петров");
        assertThat(result.getEmail()).isEqualTo("иван@пример.рф");
    }

    @Test
    void updateUser_differentEmailCase_success() {
        UpdateUserDto request = UpdateUserDto.builder()
                .id(existingUser1.getId())
                .email("JOHN@EXAMPLE.COM")
                .build();

        UserDto result = userService.updateUser(request);

        assertThat(result.getEmail()).isEqualTo("JOHN@EXAMPLE.COM");
    }
}
