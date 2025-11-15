package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ShareItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;

    private User owner;
    private User booker;
    private User anotherUser;
    private Item availableItem;
    private Item unavailableItem;
    private ItemRequest itemRequest;
    private Booking pastBooking;
    private Comment comment;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
        commentRepository.deleteAll();
        itemRequestRepository.deleteAll();

        owner = userRepository.save(User.builder()
                .name("Owner User")
                .email("owner@example.com")
                .build());

        booker = userRepository.save(User.builder()
                .name("Booker User")
                .email("booker@example.com")
                .build());

        anotherUser = userRepository.save(User.builder()
                .name("Another User")
                .email("another@example.com")
                .build());

        itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Need a drill")
                .requestor(booker)
                .created(LocalDateTime.now().minusDays(5))
                .build());

        availableItem = itemRepository.save(Item.builder()
                .owner(owner)
                .name("Power Drill")
                .description("Professional power drill for construction")
                .available(true)
                .request(itemRequest)
                .build());

        unavailableItem = itemRepository.save(Item.builder()
                .owner(owner)
                .name("Broken Hammer")
                .description("Not working hammer")
                .available(false)
                .build());

        pastBooking = bookingRepository.save(Booking.builder()
                .item(availableItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .build());

        comment = commentRepository.save(Comment.builder()
                .text("Great tool, very useful!")
                .author(booker)
                .item(availableItem)
                .created(LocalDateTime.now().minusDays(1))
                .build());
    }

    @Test
    void getItems_success() {
        Collection<ItemDto> result = itemService.getItems();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemDto::getName)
                .containsExactlyInAnyOrder("Power Drill", "Broken Hammer");
    }

    @Test
    void getItems_emptyList() {
        bookingRepository.deleteAll();
        commentRepository.deleteAll();
        itemRepository.deleteAll();

        Collection<ItemDto> result = itemService.getItems();

        assertThat(result).isEmpty();
    }

    @Test
    void getItemsByOwner_success() {
        Collection<ItemDto> result = itemService.getItemsByOwner(owner.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemDto::getName)
                .containsExactlyInAnyOrder("Power Drill", "Broken Hammer");
    }

    @Test
    void addNewItem_success() {
        NewItemDto request = NewItemDto.builder()
                .name("New Item")
                .description("New item description")
                .available(true)
                .build();

        ItemDto result = itemService.addNewItem(request, owner.getId());

        assertNotNull(result);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New Item");
        assertThat(result.getDescription()).isEqualTo("New item description");
        assertThat(result.getAvailable()).isTrue();

        Item savedItem = itemRepository.findById(result.getId()).orElseThrow();
        assertThat(savedItem.getName()).isEqualTo("New Item");
        assertThat(savedItem.getDescription()).isEqualTo("New item description");
    }

    @Test
    void addNewItem_withRequest_success() {
        NewItemDto request = NewItemDto.builder()
                .name("Requested Item")
                .description("Item created from request")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        ItemDto result = itemService.addNewItem(request, owner.getId());

        assertNotNull(result);
        assertThat(result.getName()).isEqualTo("Requested Item");
        assertThat(result.getDescription()).isEqualTo("Item created from request");

        Item savedItem = itemRepository.findById(result.getId()).orElseThrow();
        assertThat(savedItem.getRequest().getId()).isEqualTo(itemRequest.getId());
    }

    @Test
    void getItemById_successForOwner() {
        ItemBookingDateParametersDto result = itemService.getItemById(availableItem.getId(), owner.getId());

        assertThat(result.getId()).isEqualTo(availableItem.getId());
        assertThat(result.getName()).isEqualTo("Power Drill");
        assertThat(result.getDescription()).isEqualTo("Professional power drill for construction");
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void getItemById_successForOtherUser() {
        ItemBookingDateParametersDto result = itemService.getItemById(availableItem.getId(), anotherUser.getId());

        assertThat(result.getId()).isEqualTo(availableItem.getId());
        assertThat(result.getName()).isEqualTo("Power Drill");
        assertThat(result.getDescription()).isEqualTo("Professional power drill for construction");
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void updateItem_success() {
        UpdateItemDto request = UpdateItemDto.builder()
                .id(availableItem.getId())
                .owner(owner.getId())
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        ItemDto result = itemService.updateItem(request);

        assertThat(result.getId()).isEqualTo(availableItem.getId());
        assertThat(result.getName()).isEqualTo("Updated Drill");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getAvailable()).isFalse();

        Item updatedItem = itemRepository.findById(availableItem.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("Updated Drill");
        assertThat(updatedItem.getDescription()).isEqualTo("Updated description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void updateItem_onlyName_success() {
        UpdateItemDto request = UpdateItemDto.builder()
                .id(availableItem.getId())
                .owner(owner.getId())
                .name("Only Name Updated")
                .build();

        ItemDto result = itemService.updateItem(request);

        assertThat(result.getId()).isEqualTo(availableItem.getId());
        assertThat(result.getName()).isEqualTo("Only Name Updated");
        assertThat(result.getDescription()).isEqualTo("Professional power drill for construction");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void updateItem_onlyDescription_success() {
        UpdateItemDto request = UpdateItemDto.builder()
                .id(availableItem.getId())
                .owner(owner.getId())
                .description("Only description updated")
                .build();

        ItemDto result = itemService.updateItem(request);

        assertThat(result.getId()).isEqualTo(availableItem.getId());
        assertThat(result.getName()).isEqualTo("Power Drill");
        assertThat(result.getDescription()).isEqualTo("Only description updated");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void deleteItem_success() {
        itemService.deleteItem(availableItem.getId(), owner.getId());

        assertThat(itemRepository.findById(availableItem.getId())).isEmpty();
        assertThat(itemRepository.findById(unavailableItem.getId())).isPresent();
    }

    @Test
    void searchItems_success() {
        List<ItemDto> result = itemService.searchItems("drill");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Power Drill");
    }

    @Test
    void searchItems_emptyText_returnsEmptyList() {
        List<ItemDto> result = itemService.searchItems("");

        assertThat(result).isEmpty();
    }

    @Test
    void searchItems_nullText_returnsEmptyList() {
        List<ItemDto> result = itemService.searchItems(null);

        assertThat(result).isEmpty();
    }

    @Test
    void searchItems_noMatches_returnsEmptyList() {
        List<ItemDto> result = itemService.searchItems("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void searchItems_caseInsensitive_success() {
        List<ItemDto> result = itemService.searchItems("DRILL");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Power Drill");
    }

    @Test
    void addNewComment_success() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Excellent tool, highly recommended!");

        CommentDto result = itemService.addNewComment(booker.getId(), availableItem.getId(), request);

        assertNotNull(result);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getText()).isEqualTo("Excellent tool, highly recommended!");
        assertThat(result.getAuthorName()).isEqualTo("Booker User");

        Comment savedComment = commentRepository.findById(result.getId()).orElseThrow();
        assertThat(savedComment.getText()).isEqualTo("Excellent tool, highly recommended!");
        assertThat(savedComment.getAuthor().getId()).isEqualTo(booker.getId());
        assertThat(savedComment.getItem().getId()).isEqualTo(availableItem.getId());
    }

    @Test
    void getItemsByOwner_withComments_success() {
        Collection<ItemDto> result = itemService.getItemsByOwner(owner.getId());

        assertThat(result).hasSize(2);

        ItemDto itemWithComments = result.stream()
                .filter(item -> item.getName().equals("Power Drill"))
                .findFirst()
                .orElseThrow();

        assertThat(itemWithComments.getComments()).hasSize(1);
        assertThat(itemWithComments.getComments().get(0).getText()).isEqualTo("Great tool, very useful!");
    }

    @Test
    void getItemById_withBookingDates_success() {
        ItemBookingDateParametersDto result = itemService.getItemById(availableItem.getId(), owner.getId());

        assertThat(result.getId()).isEqualTo(availableItem.getId());
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void updateItem_multipleFields_success() {
        UpdateItemDto request = UpdateItemDto.builder()
                .id(availableItem.getId())
                .owner(owner.getId())
                .name("Professional Drill")
                .description("Heavy duty professional drill")
                .available(false)
                .build();

        ItemDto result = itemService.updateItem(request);

        assertThat(result.getName()).isEqualTo("Professional Drill");
        assertThat(result.getDescription()).isEqualTo("Heavy duty professional drill");
        assertThat(result.getAvailable()).isFalse();
    }
}
