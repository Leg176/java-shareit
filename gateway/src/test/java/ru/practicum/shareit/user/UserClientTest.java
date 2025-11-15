package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import java.util.function.Supplier;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.UserClient.API_PREFIX;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    private UserClient userClient;
    private static final String BASE_URL = "http://test-server";

    @BeforeEach
    void setUp() {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(BASE_URL + API_PREFIX);
        when(builder.uriTemplateHandler(any(UriTemplateHandler.class)))
                .thenReturn(builder);
        when(builder.requestFactory(any(Supplier.class)))
                .thenReturn(builder);
        when(builder.build())
                .thenReturn(restTemplate);
        builder.uriTemplateHandler(uriBuilderFactory);

        userClient = new UserClient(BASE_URL, builder);

        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body("mock-response");
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                eq(Object.class)
        )).thenReturn(mockResponse);
    }

    @Test
    void getUserById_ShouldCallGetWithIdInPath() {
        Long userId = 1L;
        userClient.getUserById(userId);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getAllUsers_ShouldCallGetWithEmptyPath() {
        userClient.getAllUsers();
        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void saveUser_ShouldCallPostWithUserData() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Test User");
        newUserDto.setEmail("test@example.com");

        userClient.saveUser(newUserDto);

        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == newUserDto),
                eq(Object.class)
        );
    }

    @Test
    void updateUser_ShouldCallPatchWithUserIdAndUpdateData() {
        Long userId = 1L;
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("Updated User");
        updateUserDto.setEmail("updated@example.com");

        userClient.updateUser(updateUserDto, userId);

        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.PATCH),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == updateUserDto),
                eq(Object.class)
        );
    }

    @Test
    void deleteUser_ShouldCallDeleteWithIdInPath() {
        Long userId = 1L;
        userClient.deleteUser(userId);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.DELETE),
                any(),
                eq(Object.class)
        );
    }
}
