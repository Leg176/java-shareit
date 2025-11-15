package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(ErrorHandler.class)
class ItemControllerTest {

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController itemController;

    private NewItemDto newItemDto;
    private UpdateItemDto updateItemDto;
    private NewCommentRequest newCommentRequest;

    @BeforeEach
    void setUp() {
        newItemDto = new NewItemDto();
        newItemDto.setName("Kyvalda");
        newItemDto.setDescription("TykTyk");
        newItemDto.setAvailable(true);

        updateItemDto = new UpdateItemDto();
        updateItemDto.setDescription("TestTest");

        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Great item!");
    }

    @Test
    void findAll_ShouldReturnAllItemsByOwner() {
        Long ownerId = 1L;
        String mockResponse = "[{\"id\":1,\"name\":\"Kyvalda\",\"description\":\"TykTyk\",\"available\":true}]";
        when(itemClient.getItemsOwner(ownerId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemController.getItemsByOwner(ownerId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemClient).getItemsOwner(ownerId);
    }

    @Test
    void getItemBookingDateParametersDto_ShouldReturnItemWithBookingDates() {
        Long ownerId = 1L;
        Long itemId = 1L;
        String mockResponse = "{\"id\":1,\"name\":\"Kyvalda\",\"description\":\"TykTyk\",\"available\":true}";
        when(itemClient.getItem(itemId, ownerId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemController.getItemBookingDateParametersDto(ownerId, itemId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemClient).getItem(itemId, ownerId);
    }

    @Test
    void searchItems_ShouldReturnSearchResults() {
        String searchText = "drill";
        String mockResponse = "[{\"id\":2,\"name\":\"Electric Drill\",\"description\":\"Powerful drill\",\"available\":true}]";
        when(itemClient.searchItems(searchText))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemController.searchItems(searchText);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemClient).searchItems(searchText);
    }

    @Test
    void searchItems_WithEmptyText_ShouldReturnEmptyList() {
        String searchText = "";
        String mockResponse = "[]";
        when(itemClient.searchItems(searchText))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemController.searchItems(searchText);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemClient).searchItems(searchText);
    }

    @Test
    void create_ShouldCreateNewItem() {
        Long ownerId = 1L;
        String mockResponse = "{\"id\":3,\"name\":\"Kyvalda\",\"description\":\"TykTyk\",\"available\":true}";
        when(itemClient.addNewItem(ownerId, newItemDto))
                .thenReturn(ResponseEntity.created(URI.create("/items/3")).body(mockResponse));

        ResponseEntity<Object> result = itemController.create(ownerId, newItemDto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getHeaders().getLocation()).hasPath("/items/3");
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemClient).addNewItem(ownerId, newItemDto);
    }

    @Test
    void update_ShouldUpdateExistingItem() {
        Long ownerId = 1L;
        Long itemId = 1L;
        String mockResponse = "{\"id\":1,\"name\":\"Updated Kyvalda\",\"description\":\"TestTest\",\"available\":true}";
        when(itemClient.updateItem(updateItemDto, ownerId, itemId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemController.update(updateItemDto, ownerId, itemId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemClient).updateItem(updateItemDto, ownerId, itemId);
    }

    @Test
    void removeItem_ShouldDeleteItem() {
        Long ownerId = 1L;
        Long itemId = 1L;

        when(itemClient.deleteItem(itemId, ownerId))
                .thenReturn(ResponseEntity.noContent().build());

        itemController.removeItem(itemId, ownerId);
        verify(itemClient).deleteItem(itemId, ownerId);
    }

    @Test
    void createComment_ShouldAddCommentToItem() {
        Long ownerId = 1L;
        Long itemId = 1L;
        String mockResponse = "{\"id\":5,\"text\":\"Great item!\",\"authorName\":\"John\"}";
        when(itemClient.addNewComment(ownerId, itemId, newCommentRequest))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemController.create(ownerId, itemId, newCommentRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemClient).addNewComment(ownerId, itemId, newCommentRequest);
    }

    @Test
    void createComment_ShouldReturnCreatedResponse() {
        Long ownerId = 1L;
        Long itemId = 1L;
        String mockResponse = "{\"id\":5,\"text\":\"Great item!\",\"authorName\":\"John\"}";
        when(itemClient.addNewComment(ownerId, itemId, newCommentRequest))
                .thenReturn(ResponseEntity.created(URI.create("/items/1/comment/5")).body(mockResponse));

        ResponseEntity<Object> result = itemController.create(ownerId, itemId, newCommentRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getHeaders().getLocation()).hasPath("/items/1/comment/5");
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemClient).addNewComment(ownerId, itemId, newCommentRequest);
    }

    @Test
    void getItemsByOwner_WhenNoItems_ShouldReturnEmptyList() {
        Long ownerId = 1L;
        String mockResponse = "[]";
        when(itemClient.getItemsOwner(ownerId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemController.getItemsByOwner(ownerId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo("[]");
        verify(itemClient).getItemsOwner(ownerId);
    }

    @Test
    void searchItems_WhenNoResults_ShouldReturnEmptyList() {
        String searchText = "none";
        String mockResponse = "[]";
        when(itemClient.searchItems(searchText))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemController.searchItems(searchText);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemClient).searchItems(searchText);
    }

    @Test
    void update_WithPartialData_ShouldUpdateItem() {
        Long ownerId = 1L;
        Long itemId = 1L;
        UpdateItemDto otherDto = new UpdateItemDto();
        otherDto.setDescription("New description only");

        String mockResponse = "{\"id\":1,\"name\":\"Kyvalda\",\"description\":\"New description only\",\"available\":true}";
        when(itemClient.updateItem(otherDto, ownerId, itemId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ResponseEntity<Object> result = itemController.update(otherDto, ownerId, itemId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) result.getBody()).isEqualTo(mockResponse);
        verify(itemClient).updateItem(otherDto, ownerId, itemId);
    }
}
