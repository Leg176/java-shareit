package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import java.util.Map;
import java.util.function.Supplier;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.constants.HttpHeaders.X_SHARER_USER_ID;
import static ru.practicum.shareit.item.ItemClient.API_PREFIX;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    private ItemClient itemClient;
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

        itemClient = new ItemClient(BASE_URL, builder);

        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body("mock-response");
        lenient().when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                eq(Object.class)
        )).thenReturn(mockResponse);
    }

    @Test
    void getItemById_ShouldCallGetWithIdInPath() {
        Long itemId = 1L;
        Long ownerId = 1L;
        itemClient.getItem(itemId, ownerId);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }

    @Test
    void getItemByOwner_ShouldCallGetWithAllPathAndUserIdHeader() {
        Long ownerId = 1L;
        itemClient.getItemsOwner(ownerId);

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
    void addNewItem_ShouldCallPostWithItemDataAndUserIdHeader() {
        Long ownerId = 1L;
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("Molotok");
        newItemDto.setDescription("TestTestTestTest");
        newItemDto.setAvailable(true);

        itemClient.addNewItem(ownerId, newItemDto);

        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == newItemDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }

    @Test
    void updateItem_ShouldCallPatchWithRequestIdAndUpdateData() {
        Long ownerId = 1L;
        Long itemId = 1L;
        UpdateItemDto updateItemDto = new UpdateItemDto();
        itemClient.updateItem(updateItemDto, ownerId, itemId);

        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.PATCH),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == updateItemDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }

    @Test
    void deleteItem_ShouldCallDeleteWithIdInPath() {
        Long ownerId = 1L;
        Long itemId = 1L;
        itemClient.deleteItem(itemId, ownerId);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(HttpMethod.DELETE),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }

   @Test
   void searchItems_ShouldCallGetWithSearchText() {
       String searchText = "testTest";

       // Специфичный стаббинг для этого теста
       ResponseEntity<Object> mockResponse = ResponseEntity.ok().body("search-result");
       when(restTemplate.exchange(
               eq("/search?text={text}"),
               eq(HttpMethod.GET),
               any(),
               eq(Object.class),
               eq(Map.of("text", searchText))
       )).thenReturn(mockResponse);

       itemClient.searchItems(searchText);

       verify(restTemplate).exchange(
               eq("/search?text={text}"),
               eq(HttpMethod.GET),
               any(),
               eq(Object.class),
               eq(Map.of("text", searchText))
       );
   }

    @Test
    void addNewComment_ShouldCallPostWithItemIdAndCommentData() {
        Long ownerId = 1L;
        Long itemId = 1L;
        NewCommentRequest request = new NewCommentRequest();
        request.setText("TestTestTestTest");

        itemClient.addNewComment(ownerId, itemId, request);

        verify(restTemplate).exchange(
                eq("/1/comment"),
                eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == request &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                entity.getHeaders().getFirst(X_SHARER_USER_ID).equals("1")),
                eq(Object.class)
        );
    }
}
