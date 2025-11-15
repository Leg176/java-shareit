package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private NewUserDto newUserDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("john")
                .email("j@d.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("john")
                .email("j@d.com")
                .build();

        newUserDto = new NewUserDto();
        newUserDto.setName("alex");
        newUserDto.setEmail("a@a.com");

        updateUserDto = new UpdateUserDto();
        updateUserDto.setId(1L);
        updateUserDto.setName("new name");
        updateUserDto.setEmail("new@mail.com");
    }

    @Test
    void getAllUsers_success() {
        User user2 = User.builder().id(2L).name("mary").email("m@d.com").build();
        UserDto userDto2 = UserDto.builder().id(2L).name("mary").email("m@d.com").build();

        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        when(userMapper.mapToUserDto(eq(user))).thenReturn(userDto);
        when(userMapper.mapToUserDto(eq(user2))).thenReturn(userDto2);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("john", result.get(0).getName());
        assertEquals("mary", result.get(1).getName());
        verify(userRepository).findAll();
        verify(userMapper, times(2)).mapToUserDto(any(User.class));
    }

    @Test
    void saveUser_success() {
        User newUser = User.builder()
                .name("alex")
                .email("a@a.com")
                .build();

        UserDto savedUserDto = UserDto.builder()
                .id(1L)
                .name("alex")
                .email("a@a.com")
                .build();

        when(userRepository.existsByEmail(eq("a@a.com"))).thenReturn(false);
        when(userMapper.mapToUser(eq(newUserDto))).thenReturn(newUser);
        when(userRepository.save(eq(newUser))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            return User.builder()
                    .id(1L)
                    .name(userToSave.getName())
                    .email(userToSave.getEmail())
                    .build();
        });
        when(userMapper.mapToUserDto(any(User.class))).thenReturn(savedUserDto);

        UserDto result = userService.saveUser(newUserDto);

        assertEquals(1L, result.getId());
        assertEquals("alex", result.getName());
        verify(userRepository).existsByEmail(eq("a@a.com"));
        verify(userMapper).mapToUser(eq(newUserDto));
        verify(userRepository).save(eq(newUser));
        verify(userMapper).mapToUserDto(any(User.class));
    }

    @Test
    void saveUser_whenEmailExists_throwsValidationException() {
        when(userRepository.existsByEmail(eq("a@a.com"))).thenReturn(true);

        assertThrows(ValidationException.class, () -> userService.saveUser(newUserDto));
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).mapToUser(any());
    }

    @Test
    void updateUser_success() {
        User updatedUser = User.builder()
                .id(1L)
                .name("new name")
                .email("new@mail.com")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("new name")
                .email("new@mail.com")
                .build();

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(eq("new@mail.com"), eq(1L))).thenReturn(false);
        when(userMapper.updateUserFields(eq(updateUserDto), eq(user))).thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.mapToUserDto(any(User.class))).thenReturn(updatedUserDto);

        UserDto result = userService.updateUser(updateUserDto);

        assertEquals(1L, result.getId());
        assertEquals("new name", result.getName());
        verify(userRepository).findById(eq(1L));
        verify(userRepository).existsByEmailAndIdNot(eq("new@mail.com"), eq(1L));
        verify(userMapper).updateUserFields(eq(updateUserDto), eq(user));
        verify(userRepository).save(any(User.class));
        verify(userMapper).mapToUserDto(any(User.class));
    }

    @Test
    void updateUser_whenUserNotFound_throwsNotFoundException() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(updateUserDto));
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).updateUserFields(any(), any());
    }

    @Test
    void updateUser_whenEmailExistsForOtherUser_throwsValidationException() {
        when(userRepository.existsByEmailAndIdNot(eq("new@mail.com"), eq(1L))).thenReturn(true);

        assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto));

        verify(userRepository, never()).findById(any());
        verify(userRepository).existsByEmailAndIdNot(eq("new@mail.com"), eq(1L));
        verify(userMapper, never()).updateUserFields(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_withOnlyName_success() {
        UpdateUserDto partialUpdate = new UpdateUserDto();
        partialUpdate.setId(1L);
        partialUpdate.setName("updated name");

        User updatedUser = User.builder()
                .id(1L)
                .name("updated name")
                .email("j@d.com")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("updated name")
                .email("j@d.com")
                .build();

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userMapper.updateUserFields(eq(partialUpdate), eq(user))).thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.mapToUserDto(any(User.class))).thenReturn(updatedUserDto);
        UserDto result = userService.updateUser(partialUpdate);

        assertEquals(1L, result.getId());
        assertEquals("updated name", result.getName());
        assertEquals("j@d.com", result.getEmail());
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
        verify(userMapper).updateUserFields(eq(partialUpdate), eq(user));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_withOnlyEmail_success() {
        UpdateUserDto partialUpdate = new UpdateUserDto();
        partialUpdate.setId(1L);
        partialUpdate.setEmail("new@mail.com");

        User updatedUser = User.builder()
                .id(1L)
                .name("john")
                .email("new@mail.com")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("john")
                .email("new@mail.com")
                .build();

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(eq("new@mail.com"), eq(1L))).thenReturn(false);
        when(userMapper.updateUserFields(eq(partialUpdate), eq(user))).thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.mapToUserDto(any(User.class))).thenReturn(updatedUserDto);

        UserDto result = userService.updateUser(partialUpdate);

        assertEquals(1L, result.getId());
        assertEquals("john", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        verify(userRepository).existsByEmailAndIdNot(eq("new@mail.com"), eq(1L));
        verify(userMapper).updateUserFields(eq(partialUpdate), eq(user));
        verify(userRepository).save(any(User.class));
        verify(userMapper).mapToUserDto(any(User.class));
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userMapper.mapToUserDto(eq(user))).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
        assertEquals("john", result.getName());
        verify(userRepository).findById(eq(1L));
        verify(userMapper).mapToUserDto(eq(user));
    }

    @Test
    void getUserById_whenUserNotFound_throwsNotFoundException() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void deleteUser_success() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(eq(1L));
    }
}