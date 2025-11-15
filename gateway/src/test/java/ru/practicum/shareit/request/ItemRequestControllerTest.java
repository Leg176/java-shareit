package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(ErrorHandler.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestClient itemRequestClient;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private NewItemRequestDto newItemRequestDto;
    private UpdateItemRequestDto updateItemRequestDto;

    @BeforeEach
    void setUp() {
        newItemRequestDto = new NewItemRequestDto("TestTestTestTest");
        updateItemRequestDto = new UpdateItemRequestDto();
        updateItemRequestDto.setDescription("TestTest");
    }

    @Test
    void findAll_ShouldReturnAllItemRequestsByOwner() {
        Long ownerId = 1L;
        String mockResponse = "[{\"id\":1,\"description\":\"Need a drill\"}]";
        when(itemRequestClient.getRequestsByOwner(ownerId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemRequestController.findAll(ownerId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemRequestClient).getRequestsByOwner(ownerId);
    }

    @Test
    void getRequest_ValidId_ShouldReturnItemRequest() {
        Long requestId = 1L;
        String mockResponse = "{\"id\":1,\"description\":\"Need a drill\"}";
        when(itemRequestClient.getRequestById(requestId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemRequestController.getRequest(requestId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemRequestClient).getRequestById(requestId);
    }

    @Test
    void getRequestNotOwner_ShouldReturnOtherUsersItemRequests() {
        Long userId = 1L;
        String mockResponse = "[{\"id\":3,\"description\":\"Need a hammer\"}]";
        when(itemRequestClient.getRequestsByNotOwner(userId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemRequestController.getRequestNotOwner(userId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemRequestClient).getRequestsByNotOwner(userId);
    }

    @Test
    void create_ShouldCallClientWithRequestDataAndUserId() {
        Long ownerId = 1L;
        String mockResponse = "{\"id\":5,\"description\":\"TestTestTestTest\"}";
        when(itemRequestClient.addNewRequest(newItemRequestDto, ownerId))
                .thenReturn(ResponseEntity.created(URI.create("/requests/5")).body(mockResponse));

        ResponseEntity<Object> result = itemRequestController.create(ownerId, newItemRequestDto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getHeaders().getLocation()).hasPath("/requests/5");
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemRequestClient).addNewRequest(newItemRequestDto, ownerId);
    }

    @Test
    void update_ValidRequest_ShouldUpdateItemRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        String mockResponse = "{\"id\":1,\"description\":\"TestTest\"}";
        when(itemRequestClient.updateRequest(updateItemRequestDto, userId, requestId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemRequestController.update(updateItemRequestDto, userId, requestId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemRequestClient).updateRequest(updateItemRequestDto, userId, requestId);
    }

    @Test
    void removeRequest_ValidId_ShouldDeleteItemRequest() {
        Long ownerId = 1L;
        Long requestId = 1L;

        when(itemRequestClient.deleteRequest(requestId, ownerId))
                .thenReturn(ResponseEntity.noContent().build());

        itemRequestController.removeRequest(requestId, ownerId);
        verify(itemRequestClient).deleteRequest(requestId, ownerId);
    }
}
