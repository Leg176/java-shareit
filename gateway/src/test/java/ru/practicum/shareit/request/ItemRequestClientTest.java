package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import java.util.function.Supplier;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    private ItemRequestClient itemRequestClient;
    private static final String BASE_URL = "http://test-server";
    private static final String API_PREFIX = "/requests";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

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

        itemRequestClient = new ItemRequestClient(BASE_URL, builder);

        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body("mock-response");
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                eq(Object.class)
        )).thenReturn(mockResponse);
    }

    @Test
    void getItemRequestById_ShouldCallGetWithIdInPath() {
        Long userId = 1L;
        itemRequestClient.getRequestById(userId);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getAllItemRequests_ShouldCallGetWithEmptyPath() {
        Long userId = 1L;
        itemRequestClient.getRequestsByNotOwner(userId);
        verify(restTemplate).exchange(
                eq("/all"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getItemRequestsByNotOwner_ShouldCallGetWithAllPathAndUserIdHeader() {
        Long userId = 1L;
        itemRequestClient.getRequestsByNotOwner(userId);

        verify(restTemplate).exchange(
                eq("/all"),
                eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }

    @Test
    void getItemRequestsByOwner_ShouldCallGetWithEmptyPathAndUserIdHeader() {
        Long ownerId = 1L;
        itemRequestClient.getRequestsByOwner(ownerId);

        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }

    @Test
    void saveItemRequest_ShouldCallPostWithRequestData() {
        Long ownerId = 1L;
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("TestTestTestTest");

        itemRequestClient.addNewRequest(newItemRequestDto, ownerId);

        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == newItemRequestDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }

    @Test
    void updateItemRequest_ShouldCallPatchWithRequestIdAndUpdateData() {
        Long ownerId = 1L;
        Long requestId = 1L;
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();
        updateItemRequestDto.setDescription("TestTest");

        itemRequestClient.updateRequest(updateItemRequestDto, ownerId, requestId);

        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.PATCH),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == updateItemRequestDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }

    @Test
    void deleteItemRequest_ShouldCallDeleteWithIdInPath() {
        Long ownerId = 1L;
        Long requestId = 1L;
        itemRequestClient.deleteRequest(requestId, ownerId);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.DELETE),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }
}
