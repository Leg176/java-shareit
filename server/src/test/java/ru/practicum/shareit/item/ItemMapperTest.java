package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    private User owner;
    private User requestor;
    private ItemRequest itemRequest;
    private Item item;
    private NewItemDto newItemDto;
    private UpdateItemDto updateItemDto;
    private CommentDto commentDto;
    private BookingTimeDto lastBooking;
    private BookingTimeDto nextBooking;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        requestor = User.builder()
                .id(2L)
                .name("Jane")
                .email("jane@example.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(10L)
                .description("need tools")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        newItemDto = NewItemDto.builder()
                .name("Hammer")
                .description("Steel hammer")
                .available(true)
                .build();

        updateItemDto = UpdateItemDto.builder()
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Great tool!")
                .authorName("Jane")
                .created(LocalDateTime.now())
                .build();

        lastBooking = BookingTimeDto.builder()
                .id(1L)
                .bookerId(2L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        nextBooking = BookingTimeDto.builder()
                .id(2L)
                .bookerId(3L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void mapToItemDto_shouldMapAllFields() {
        ItemDto result = itemMapper.mapToItemDto(item);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getOwner()).isEqualTo("John");
        assertThat(result.getRequestId()).isEqualTo(10L);
    }

    @Test
    void mapToItemDto_shouldHandleNullRequest() {
        Item itemWithoutRequest = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        ItemDto result = itemMapper.mapToItemDto(itemWithoutRequest);

        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void mapToItemAndCommentsDto_shouldMapItemAndComments() {
        List<CommentDto> comments = List.of(commentDto);

        ItemDto result = itemMapper.mapToItemAndCommentsDto(item, comments);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getOwner()).isEqualTo("John");
        assertThat(result.getRequestId()).isEqualTo(10L);
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getText()).isEqualTo("Great tool!");
    }

    @Test
    void mapToItemAndCommentsDto_shouldHandleEmptyComments() {
        ItemDto result = itemMapper.mapToItemAndCommentsDto(item, Collections.emptyList());

        assertThat(result.getComments()).isEmpty();
    }

    @Test
    void mapToItemBookingDateParametersDto_shouldMapAllFieldsWithBookingsAndComments() {
        List<CommentDto> comments = List.of(commentDto);

        ItemBookingDateParametersDto result = itemMapper.mapToItemBookingDateParametersDto(
                item, lastBooking, nextBooking, comments);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getOwner()).isEqualTo("John");
        assertThat(result.getRequestId()).isEqualTo(10L);
        assertThat(result.getLastBooking()).isEqualTo(lastBooking);
        assertThat(result.getNextBooking()).isEqualTo(nextBooking);
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void mapToItemBookingDateParametersDto_shouldHandleNullBookings() {
        List<CommentDto> comments = List.of(commentDto);

        ItemBookingDateParametersDto result = itemMapper.mapToItemBookingDateParametersDto(
                item, null, null, comments);

        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void mapToItemParametersDto_shouldMapItemWithComments() {
        List<CommentDto> comments = List.of(commentDto);

        ItemBookingDateParametersDto result = itemMapper.mapToItemParametersDto(item, comments);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getOwner()).isEqualTo("John");
        assertThat(result.getRequestId()).isEqualTo(10L);
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void mapToItem_shouldMapNewItemDtoWithoutRequest() {
        Item result = itemMapper.mapToItem(newItemDto, owner);

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Hammer");
        assertThat(result.getDescription()).isEqualTo("Steel hammer");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getRequest()).isNull();
    }

    @Test
    void mapToItem_shouldMapNewItemDtoWithRequest() {
        Item result = itemMapper.mapToItem(newItemDto, owner, itemRequest);

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Hammer");
        assertThat(result.getDescription()).isEqualTo("Steel hammer");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getRequest()).isEqualTo(itemRequest);
    }

    @Test
    void mapToItem_shouldHandleNullUser() {
        Item result = itemMapper.mapToItem(newItemDto, null);

        assertThat(result.getOwner()).isNull();
    }

    @Test
    void mapToItem_shouldHandleNullRequest() {
        Item result = itemMapper.mapToItem(newItemDto, owner, null);

        assertThat(result.getRequest()).isNull();
    }

    @Test
    void updateItemFields_shouldUpdateAllFields() {
        Item result = itemMapper.updateItemFields(item, updateItemDto);

        assertThat(result).isSameAs(item);
        assertThat(item.getName()).isEqualTo("Updated Drill");
        assertThat(item.getDescription()).isEqualTo("Updated description");
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    void updateItemFields_shouldUpdateOnlyName() {
        UpdateItemDto nameOnlyUpdate = UpdateItemDto.builder()
                .name("New Name")
                .build();

        Item result = itemMapper.updateItemFields(item, nameOnlyUpdate);

        assertThat(result).isSameAs(item);
        assertThat(item.getName()).isEqualTo("New Name");
        assertThat(item.getDescription()).isEqualTo("Powerful drill");
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    void updateItemFields_shouldUpdateOnlyDescription() {
        UpdateItemDto descriptionOnlyUpdate = UpdateItemDto.builder()
                .description("New description")
                .build();

        Item result = itemMapper.updateItemFields(item, descriptionOnlyUpdate);

        assertThat(result).isSameAs(item);
        assertThat(item.getName()).isEqualTo("Drill");
        assertThat(item.getDescription()).isEqualTo("New description");
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    void updateItemFields_shouldUpdateOnlyAvailability() {
        UpdateItemDto availabilityOnlyUpdate = UpdateItemDto.builder()
                .available(false)
                .build();

        Item result = itemMapper.updateItemFields(item, availabilityOnlyUpdate);

        assertThat(result).isSameAs(item);
        assertThat(item.getName()).isEqualTo("Drill");
        assertThat(item.getDescription()).isEqualTo("Powerful drill");
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    void updateItemFields_shouldNotUpdateWhenNoChanges() {
        UpdateItemDto emptyUpdate = UpdateItemDto.builder().build();

        Item result = itemMapper.updateItemFields(item, emptyUpdate);

        assertThat(result).isSameAs(item);
        assertThat(item.getName()).isEqualTo("Drill");
        assertThat(item.getDescription()).isEqualTo("Powerful drill");
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    void mapToItemAndCommentsDto_shouldHandleNullOwner() {
        Item itemWithNullOwner = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(null)
                .request(itemRequest)
                .build();

        ItemDto result = itemMapper.mapToItemAndCommentsDto(itemWithNullOwner, List.of(commentDto));

        assertThat(result.getOwner()).isNull();
    }

    @Test
    void mapToItemAndCommentsDto_shouldHandleNullOwnerName() {
        User ownerWithoutName = User.builder()
                .id(1L)
                .name(null)
                .email("john@example.com")
                .build();

        Item itemWithNullOwnerName = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(ownerWithoutName)
                .request(itemRequest)
                .build();

        ItemDto result = itemMapper.mapToItemAndCommentsDto(itemWithNullOwnerName, List.of(commentDto));

        assertThat(result.getOwner()).isNull();
    }

    @Test
    void mapToItemBookingDateParametersDto_shouldHandleNullOwner() {
        Item itemWithNullOwner = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(null)
                .request(itemRequest)
                .build();

        ItemBookingDateParametersDto result = itemMapper.mapToItemBookingDateParametersDto(
                itemWithNullOwner, lastBooking, nextBooking, List.of(commentDto));

        assertThat(result.getOwner()).isNull();
    }

    @Test
    void mapToItemBookingDateParametersDto_shouldHandleNullComments() {
        ItemBookingDateParametersDto result = itemMapper.mapToItemBookingDateParametersDto(
                item, lastBooking, nextBooking, null);

        assertThat(result.getComments()).isNotNull().isEmpty();
    }

    @Test
    void mapToItemParametersDto_shouldHandleNullComments() {
        ItemBookingDateParametersDto result = itemMapper.mapToItemParametersDto(item, null);

        assertThat(result.getComments()).isNotNull().isEmpty();
    }

    @Test
    void mapToItemAndCommentsDto_shouldHandleNullComments() {
        ItemDto result = itemMapper.mapToItemAndCommentsDto(item, null);

        assertThat(result.getComments()).isNotNull().isEmpty();
    }

    @Test
    void mapToItemParametersDto_shouldHandleNullOwner() {
        Item itemWithNullOwner = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(null)
                .request(itemRequest)
                .build();

        ItemBookingDateParametersDto result = itemMapper.mapToItemParametersDto(itemWithNullOwner, List.of(commentDto));

        assertThat(result.getOwner()).isNull();
    }

    @Test
    void mapToItemAndCommentsDto_shouldHandleNullItem() {
        ItemDto result = itemMapper.mapToItemAndCommentsDto(null, List.of(commentDto));

        assertThat(result).isNotNull();
    }

    @Test
    void mapToItemDto_shouldHandleNullOwner() {
        Item itemWithNullOwner = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(null)
                .request(itemRequest)
                .build();

        ItemDto result = itemMapper.mapToItemDto(itemWithNullOwner);

        assertThat(result.getOwner()).isNull();
    }

    @Test
    void mapToItemDto_shouldHandleNullOwnerName() {
        User ownerWithoutName = User.builder()
                .id(1L)
                .name(null)
                .email("john@example.com")
                .build();

        Item itemWithNullOwnerName = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(ownerWithoutName)
                .request(itemRequest)
                .build();

        ItemDto result = itemMapper.mapToItemDto(itemWithNullOwnerName);

        assertThat(result.getOwner()).isNull();
    }

    @Test
    void mapToItemBookingDateParametersDto_shouldHandleNullItem() {
        ItemBookingDateParametersDto result = itemMapper.mapToItemBookingDateParametersDto(
                null, lastBooking, nextBooking, List.of(commentDto));

        assertThat(result).isNotNull();
    }

    @Test
    void mapToItemParametersDto_shouldHandleNullItem() {
        ItemBookingDateParametersDto result = itemMapper.mapToItemParametersDto(null, List.of(commentDto));

        assertThat(result).isNotNull();
    }

    @Test
    void mapToItemBookingDateParametersDto_shouldHandleNullOwnerInItem() {
        Item itemWithNullOwner = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(null)
                .request(null)
                .build();

        ItemBookingDateParametersDto result = itemMapper.mapToItemBookingDateParametersDto(
                itemWithNullOwner, lastBooking, nextBooking, List.of(commentDto));

        assertThat(result.getOwner()).isNull();
        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void mapToItemParametersDto_shouldHandleNullRequest() {
        Item itemWithNullRequest = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        ItemBookingDateParametersDto result = itemMapper.mapToItemParametersDto(itemWithNullRequest, List.of(commentDto));

        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void mapToItemAndCommentsDto_shouldHandleNullRequest() {
        Item itemWithNullRequest = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        ItemDto result = itemMapper.mapToItemAndCommentsDto(itemWithNullRequest, List.of(commentDto));

        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void mapToItemBookingDateParametersDto_shouldHandleNullOwnerAndNullRequest() {
        Item itemWithNullOwnerAndRequest = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(null)
                .request(null)
                .build();

        ItemBookingDateParametersDto result = itemMapper.mapToItemBookingDateParametersDto(
                itemWithNullOwnerAndRequest, lastBooking, nextBooking, List.of(commentDto));

        assertThat(result.getOwner()).isNull();
        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void mapToItemParametersDto_shouldHandleNullOwnerAndNullRequest() {
        Item itemWithNullOwnerAndRequest = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(null)
                .request(null)
                .build();

        ItemBookingDateParametersDto result = itemMapper.mapToItemParametersDto(itemWithNullOwnerAndRequest, List.of(commentDto));

        assertThat(result.getOwner()).isNull();
        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void mapToItemAndCommentsDto_shouldHandleNullOwnerAndNullRequest() {
        Item itemWithNullOwnerAndRequest = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(null)
                .request(null)
                .build();

        ItemDto result = itemMapper.mapToItemAndCommentsDto(itemWithNullOwnerAndRequest, List.of(commentDto));

        assertThat(result.getOwner()).isNull();
        assertThat(result.getRequestId()).isNull();
    }
}
