package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemRequestMapperTest {

    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    private User userJohn;
    private User userJane;
    private ItemRequest itemRequest;
    private Item itemDrill;
    private Item itemHammer;
    private NewItemRequestDto newRequestDto;
    private UpdateItemRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        userJohn = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        userJane = User.builder()
                .id(2L)
                .name("Jane")
                .email("jane@example.com")
                .build();

        itemDrill = Item.builder()
                .id(1L)
                .name("Drill")
                .owner(userJane)
                .build();

        itemHammer = Item.builder()
                .id(2L)
                .name("Hammer")
                .owner(userJane)
                .build();

        itemRequest = ItemRequest.builder()
                .id(10L)
                .description("need tools")
                .requestor(userJohn)
                .created(LocalDateTime.now())
                .items(List.of(itemDrill, itemHammer))
                .build();

        newRequestDto = new NewItemRequestDto();
        newRequestDto.setDescription("need drill");

        updateRequestDto = new UpdateItemRequestDto();
        updateRequestDto.setDescription("updated description");
    }

    @Test
    void mapToRequest_shouldMapAllFields() {
        ItemRequest result = itemRequestMapper.mapToRequest(newRequestDto, userJohn);

        assertThat(result.getId()).isNull();
        assertThat(result.getDescription()).isEqualTo("need drill");
        assertThat(result.getRequestor()).isEqualTo(userJohn);
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void mapToRequest_shouldHandleNullRequestor() {
        ItemRequest result = itemRequestMapper.mapToRequest(newRequestDto, null);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("need drill");
        assertThat(result.getRequestor()).isNull();
    }

    @Test
    void mapToRequestDto_shouldMapAllFieldsWithoutItems() {
        ItemRequest requestWithoutItems = ItemRequest.builder()
                .id(10L)
                .description("need drill")
                .requestor(userJohn)
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        ItemRequestDto result = itemRequestMapper.mapToRequestDto(requestWithoutItems);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDescription()).isEqualTo("need drill");
        assertThat(result.getRequestor()).isEqualTo("John");
        assertThat(result.getCreated()).isNotNull();
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void mapToRequestDto_shouldMapWithItems() {
        ItemRequestDto result = itemRequestMapper.mapToRequestDto(itemRequest);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDescription()).isEqualTo("need tools");
        assertThat(result.getRequestor()).isEqualTo("John");
        assertThat(result.getCreated()).isNotNull();
        assertThat(result.getItems()).hasSize(2);

        ItemInItemRequestDto itemDto1 = result.getItems().get(0);
        assertThat(itemDto1.getId()).isEqualTo(1L);
        assertThat(itemDto1.getName()).isEqualTo("Drill");
        assertThat(itemDto1.getOwnerId()).isEqualTo(2L);

        ItemInItemRequestDto itemDto2 = result.getItems().get(1);
        assertThat(itemDto2.getId()).isEqualTo(2L);
        assertThat(itemDto2.getName()).isEqualTo("Hammer");
        assertThat(itemDto2.getOwnerId()).isEqualTo(2L);
    }

    @Test
    void mapToRequestDtoNotItems_shouldMapWithoutItems() {
        ItemRequestWithoutItemsDto result = itemRequestMapper.mapToRequestDtoNotItems(itemRequest);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDescription()).isEqualTo("need tools");
        assertThat(result.getRequestor()).isEqualTo("John");
        assertThat(result.getCreated()).isNotNull();
    }


    @Test
    void updateRequestFields_shouldUpdateDescription() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("old description")
                .requestor(userJohn)
                .created(LocalDateTime.now())
                .build();

        ItemRequest result = itemRequestMapper.updateRequestFields(request, updateRequestDto);

        assertThat(result).isSameAs(request);
        assertThat(request.getDescription()).isEqualTo("updated description");
    }

    @Test
    void updateRequestFields_shouldNotUpdateWhenNoDescription() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("old description")
                .requestor(userJohn)
                .created(LocalDateTime.now())
                .build();

        UpdateItemRequestDto emptyUpdate = new UpdateItemRequestDto(); // без description

        ItemRequest result = itemRequestMapper.updateRequestFields(request, emptyUpdate);

        assertThat(result).isSameAs(request);
        assertThat(request.getDescription()).isEqualTo("old description");
    }

    @Test
    void updateRequestFields_shouldTrimDescription() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("old description")
                .requestor(userJohn)
                .created(LocalDateTime.now())
                .build();

        UpdateItemRequestDto updateWithSpaces = new UpdateItemRequestDto();
        updateWithSpaces.setDescription("  new description  ");

        ItemRequest result = itemRequestMapper.updateRequestFields(request, updateWithSpaces);

        assertThat(result).isSameAs(request);
        assertThat(request.getDescription()).isEqualTo("  new description  ");
    }

    @Test
    void updateRequestFields_shouldHandleEmptyDescription() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("old description")
                .requestor(userJohn)
                .created(LocalDateTime.now())
                .build();

        UpdateItemRequestDto updateWithEmpty = new UpdateItemRequestDto();
        updateWithEmpty.setDescription("");

        ItemRequest result = itemRequestMapper.updateRequestFields(request, updateWithEmpty);

        assertThat(result).isSameAs(request);
        assertThat(request.getDescription()).isEqualTo("old description");
    }

    @Test
    void updateRequestFields_shouldHandleNullDescription() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("old description")
                .requestor(userJohn)
                .created(LocalDateTime.now())
                .build();

        UpdateItemRequestDto updateWithNull = new UpdateItemRequestDto();
        updateWithNull.setDescription(null);

        ItemRequest result = itemRequestMapper.updateRequestFields(request, updateWithNull);

        assertThat(result).isSameAs(request);
        assertThat(request.getDescription()).isEqualTo("old description");
    }

    @Test
    void updateRequestFields_shouldThrowWhenNullUpdateRequest() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(userJohn)
                .created(LocalDateTime.now())
                .build();

        assertThatThrownBy(() -> itemRequestMapper.updateRequestFields(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UpdateRequest не может быть пустым!");
    }

    @Test
    void mapToRequestDto_shouldHandleNullItem() {
        ItemRequest requestWithNullItems = ItemRequest.builder()
                .id(10L)
                .description("need tools")
                .requestor(userJohn)
                .created(LocalDateTime.now())
                .items(null)
                .build();

        ItemRequestDto result = itemRequestMapper.mapToRequestDto(requestWithNullItems);

        assertThat(result.getItems()).isEmpty();
    }
}
