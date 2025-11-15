package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(ErrorHandler.class)
class UserControllerTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    private NewUserDto newUserDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        newUserDto = new NewUserDto("User", "test@email.com");
        updateUserDto = new UpdateUserDto();
        updateUserDto.setName("Updated Name");
        updateUserDto.setEmail("updated@email.com");
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        String mockResponse = "[{\"id\":1,\"name\":\"User1\",\"email\":\"user1@test.com\"}," +
                "{\"id\":2,\"name\":\"User2\",\"email\":\"user2@test.com\"}]";
        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok(mockResponse));
        ResponseEntity<Object> result = userController.findAll();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).contains("User1", "User2");
        verify(userClient).getAllUsers();
    }

    @Test
    void getUser_ValidId_ShouldReturnUser() {
        Long userId = 1L;
        String mockResponse = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@user.com\"}";
        when(userClient.getUserById(userId))
                .thenReturn(ResponseEntity.ok(mockResponse));
        ResponseEntity<Object> result = userController.getUser(userId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).contains("Test User", "test@user.com");
        verify(userClient).getUserById(userId);
    }

    @Test
    void create_ValidDto_ShouldCreateUser() {
        String mockResponse = "{\"id\":3,\"name\":\"New User\",\"email\":\"new@user.com\"}";
        when(userClient.saveUser(newUserDto))
                .thenReturn(ResponseEntity.created(URI.create("/users/3")).body(mockResponse));
        ResponseEntity<Object> result = userController.create(newUserDto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getHeaders().getLocation()).hasPath("/users/3");
        assertThat((String) result.getBody()).contains("New User");
        verify(userClient).saveUser(newUserDto);
    }

    @Test
    void update_ValidRequest_ShouldUpdateUser() {
        Long userId = 1L;
        String mockResponse = "{\"id\":1,\"name\":\"Updated Name\",\"email\":\"updated@email.com\"}";
        when(userClient.updateUser(updateUserDto, userId))
                .thenReturn(ResponseEntity.ok(mockResponse));
        ResponseEntity<Object> result = userController.update(userId, updateUserDto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).contains("Updated Name", "updated@email.com");
        verify(userClient).updateUser(updateUserDto, userId);
    }

    @Test
    void deleteUser_ValidId_ShouldDelete() {
        Long userId = 1L;
        when(userClient.deleteUser(userId))
                .thenReturn(ResponseEntity.noContent().build());
        ResponseEntity<Object> result = userController.deleteUser(userId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(userClient).deleteUser(userId);
    }
}
