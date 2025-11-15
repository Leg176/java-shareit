package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl service;

    private User owner;
    private User author;
    private Item item;
    private ItemDto itemDto;
    private ItemBookingDateParametersDto itemWithBookingDto;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setup() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.com")
                .build();

        author = User.builder()
                .id(2L)
                .name("author")
                .email("author@mail.com")
                .build();

        item = Item.builder()
                .id(100L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build();

        itemDto = ItemDto.builder()
                .id(100L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner("owner")
                .comments(new ArrayList<>())
                .build();

        itemWithBookingDto = ItemBookingDateParametersDto.builder()
                .id(100L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner("owner")
                .comments(new ArrayList<>())
                .build();

        comment = Comment.builder()
                .id(50L)
                .text("Great item!")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(50L)
                .text("Great item!")
                .authorName("author")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getItems_success() {
        List<Item> items = List.of(item);
        when(itemRepository.findAll()).thenReturn(items);
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        Collection<ItemDto> result = service.getItems();

        assertEquals(1, result.size());
        verify(itemRepository).findAll();
        verify(itemMapper).mapToItemDto(item);
    }

    @Test
    void getItemsByOwner_success() {
        List<Item> items = List.of(item);
        List<Comment> comments = List.of(comment);
        List<CommentDto> commentsDto = List.of(commentDto);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findByOwnerId(1L)).thenReturn(items);
        when(commentRepository.findByItemIdIn(List.of(100L))).thenReturn(comments);
        when(commentMapper.mapToCommentDtoList(comments)).thenReturn(commentsDto);
        when(itemMapper.mapToItemAndCommentsDto(item, commentsDto)).thenReturn(itemDto);

        Collection<ItemDto> result = service.getItemsByOwner(1L);

        assertEquals(1, result.size());
        verify(userRepository).existsById(1L);
        verify(itemRepository).findByOwnerId(1L);
        verify(commentRepository).findByItemIdIn(List.of(100L));
        verify(itemMapper).mapToItemAndCommentsDto(item, commentsDto);
    }

    @Test
    void addNewItem_success() {
        NewItemDto newItem = new NewItemDto();
        newItem.setName("New Drill");
        newItem.setDescription("New description");
        newItem.setAvailable(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.mapToItem(newItem, owner)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        ItemDto result = service.addNewItem(newItem, 1L);

        assertEquals(100L, result.getId());
        verify(userRepository).findById(1L);
        verify(itemMapper).mapToItem(newItem, owner);
        verify(itemRepository).save(item);
        verify(itemMapper).mapToItemDto(item);
    }

    @Test
    void addNewItem_withRequest_success() {
        NewItemDto newItem = new NewItemDto();
        newItem.setName("New Drill");
        newItem.setDescription("New description");
        newItem.setAvailable(true);
        newItem.setRequestId(200L);

        ItemRequest itemRequest = ItemRequest.builder().id(200L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(200L)).thenReturn(Optional.of(itemRequest));
        when(itemMapper.mapToItem(newItem, owner, itemRequest)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        ItemDto result = service.addNewItem(newItem, 1L);

        verify(userRepository).findById(1L);
        verify(itemRequestRepository).findById(200L);
        verify(itemMapper).mapToItem(newItem, owner, itemRequest);
        verify(itemRepository).save(item);
    }

    @Test
    void getItemById_owner_success() {
        List<Comment> comments = List.of(comment);
        List<CommentDto> commentsDto = List.of(commentDto);
        Booking lastBooking = Booking.builder().id(300L).build();
        Booking nextBooking = Booking.builder().id(301L).build();
        BookingTimeDto lastBookingDto = BookingTimeDto.builder().id(300L).build();
        BookingTimeDto nextBookingDto = BookingTimeDto.builder().id(301L).build();

        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(100L)).thenReturn(comments);
        when(commentMapper.mapToCommentDto(comment)).thenReturn(commentDto);
        when(bookingRepository.findTopByItemIdAndStatusAndEndLessThanOrderByEndDesc(
                eq(100L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findTopByItemIdAndStatusAndStartGreaterThanOrderByStartAsc(
                eq(100L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(nextBooking));
        when(bookingMapper.mapToBookingTimeDto(lastBooking)).thenReturn(lastBookingDto);
        when(bookingMapper.mapToBookingTimeDto(nextBooking)).thenReturn(nextBookingDto);
        when(itemMapper.mapToItemBookingDateParametersDto(item, lastBookingDto, nextBookingDto, commentsDto))
                .thenReturn(itemWithBookingDto);

        ItemBookingDateParametersDto result = service.getItemById(100L, 1L);

        assertEquals(100L, result.getId());
        verify(itemRepository).findById(100L);
        verify(bookingRepository).findTopByItemIdAndStatusAndEndLessThanOrderByEndDesc(
                eq(100L), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(itemMapper).mapToItemBookingDateParametersDto(item, lastBookingDto, nextBookingDto, commentsDto);
    }

    @Test
    void getItemById_notOwner_success() {
        User otherUser = User.builder().id(2L).build();
        item.setOwner(otherUser);
        List<Comment> comments = List.of(comment);
        List<CommentDto> commentsDto = List.of(commentDto);

        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(100L)).thenReturn(comments);
        when(commentMapper.mapToCommentDto(comment)).thenReturn(commentDto);
        when(itemMapper.mapToItemParametersDto(item, commentsDto)).thenReturn(itemWithBookingDto);

        ItemBookingDateParametersDto result = service.getItemById(100L, 1L);

        assertEquals(100L, result.getId());
        verify(bookingRepository, never()).findTopByItemIdAndStatusAndEndLessThanOrderByEndDesc(any(), any(), any());
        verify(itemMapper).mapToItemParametersDto(item, commentsDto);
    }

    @Test
    void updateItem_success() {
        UpdateItemDto updateItem = new UpdateItemDto();
        updateItem.setId(100L);
        updateItem.setName("Updated Drill");
        updateItem.setOwner(1L);

        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.updateItemFields(item, updateItem)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        ItemDto result = service.updateItem(updateItem);

        assertEquals(100L, result.getId());
        verify(itemRepository).findById(100L);
        verify(userRepository).findById(1L);
        verify(itemMapper).updateItemFields(item, updateItem);
        verify(itemRepository).save(item);
    }

    @Test
    void deleteItem_success() {
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));

        service.deleteItem(100L, 1L);

        verify(itemRepository).delete(item);
    }

    @Test
    void searchItems_success() {
        List<Item> items = List.of(item);
        when(itemRepository.searchAvailableItems("drill")).thenReturn(items);
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = service.searchItems("drill");

        assertEquals(1, result.size());
        verify(itemRepository).searchAvailableItems("drill");
        verify(itemMapper).mapToItemDto(item);
    }

    @Test
    void searchItems_emptyText_returnsEmptyList() {
        List<ItemDto> result = service.searchItems("");

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchAvailableItems(any());
    }

    @Test
    void searchItems_nullText_returnsEmptyList() {
        List<ItemDto> result = service.searchItems(null);

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchAvailableItems(any());
    }

    @Test
    void addNewComment_success() {
        NewCommentRequest newComment = new NewCommentRequest();
        newComment.setText("Great item!");

        when(userRepository.findById(2L)).thenReturn(Optional.of(author));
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(eq(2L), eq(100L), eq(BookingStatus.APPROVED)))
                .thenReturn(true);
        when(commentMapper.mapToComment(newComment, author, item)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.mapToCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = service.addNewComment(2L, 100L, newComment);

        assertEquals(50L, result.getId());
        verify(userRepository).findById(2L);
        verify(itemRepository).findById(100L);
        verify(bookingRepository).existsByBookerIdAndItemIdAndEndBefore(eq(2L), eq(100L), eq(BookingStatus.APPROVED));
        verify(commentMapper).mapToComment(newComment, author, item);
        verify(commentRepository).save(comment);
    }
}