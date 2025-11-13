package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithoutItemsDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private User requestor;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestWithoutItemsDto withoutItemsDto;

    @BeforeEach
    void setup() {
        requestor = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(100L)
                .description("need drill")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(100L)
                .description("need drill")
                .requestor("user")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        withoutItemsDto = ItemRequestWithoutItemsDto.builder()
                .id(100L)
                .description("need drill")
                .requestor("user")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getRequestsByOwner_success() {
        List<ItemRequest> requests = List.of(itemRequest);
        when(itemRequestRepository.findUserRequestsWithItems(1L)).thenReturn(requests);
        when(itemRequestMapper.mapToRequestDto(itemRequest)).thenReturn(itemRequestDto);

        Collection<ItemRequestDto> result = service.getRequestsByOwner(1L);

        assertEquals(1, result.size());
        assertEquals(100L, result.iterator().next().getId());
        verify(itemRequestRepository).findUserRequestsWithItems(1L);
        verify(itemRequestMapper).mapToRequestDto(itemRequest);
    }

    @Test
    void getRequestsByNotOwner_success() {
        List<ItemRequest> requests = List.of(itemRequest);
        when(itemRequestRepository.findByRequestorIdNot(1L)).thenReturn(requests);
        when(itemRequestMapper.mapToRequestDtoNotItems(itemRequest)).thenReturn(withoutItemsDto);

        Collection<ItemRequestWithoutItemsDto> result = service.getRequestsByNotOwner(1L);

        assertEquals(1, result.size());
        assertEquals(100L, result.iterator().next().getId());
        verify(itemRequestRepository).findByRequestorIdNot(1L);
        verify(itemRequestMapper).mapToRequestDtoNotItems(itemRequest);
    }

    @Test
    void addNewRequest_success() {
        NewItemRequestDto newRequest = new NewItemRequestDto();
        newRequest.setDescription("need drill");

        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        when(itemRequestMapper.mapToRequest(newRequest, requestor)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.mapToRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = service.addNewRequest(newRequest, 1L);

        assertEquals(100L, result.getId());
        assertEquals("need drill", result.getDescription());
        verify(userRepository).findById(1L);
        verify(itemRequestMapper).mapToRequest(newRequest, requestor);
        verify(itemRequestRepository).save(itemRequest);
        verify(itemRequestMapper).mapToRequestDto(itemRequest);
    }

    @Test
    void addNewRequest_userNotFound_throws() {
        NewItemRequestDto newRequest = new NewItemRequestDto();
        newRequest.setDescription("need drill");

        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addNewRequest(newRequest, 9L));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void updateRequest_success() {
        UpdateItemRequestDto updateRequest = new UpdateItemRequestDto();
        updateRequest.setId(100L);
        updateRequest.setDescription("updated description");

        when(itemRequestRepository.findByIdWithItems(100L)).thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        when(itemRequestMapper.updateRequestFields(itemRequest, updateRequest)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.mapToRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = service.updateRequest(updateRequest, 1L);

        assertEquals(100L, result.getId());
        verify(itemRequestRepository).findByIdWithItems(100L);
        verify(userRepository).findById(1L);
        verify(itemRequestMapper).updateRequestFields(itemRequest, updateRequest);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void updateRequest_requestNotFound_throws() {
        UpdateItemRequestDto updateRequest = new UpdateItemRequestDto();
        updateRequest.setId(500L);

        when(itemRequestRepository.findByIdWithItems(500L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateRequest(updateRequest, 1L));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void updateRequest_userNotFound_throws() {
        UpdateItemRequestDto updateRequest = new UpdateItemRequestDto();
        updateRequest.setId(100L);

        when(itemRequestRepository.findByIdWithItems(100L)).thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateRequest(updateRequest, 9L));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void updateRequest_notOwner_throwsValidationException() {
        UpdateItemRequestDto updateRequest = new UpdateItemRequestDto();
        updateRequest.setId(100L);

        User otherUser = User.builder().id(2L).name("other").email("other@mail.com").build();
        ItemRequest requestWithOtherOwner = ItemRequest.builder()
                .id(100L)
                .description("need drill")
                .requestor(otherUser)
                .created(LocalDateTime.now())
                .build();

        when(itemRequestRepository.findByIdWithItems(100L)).thenReturn(Optional.of(requestWithOtherOwner));
        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));

        assertThrows(ValidationException.class, () -> service.updateRequest(updateRequest, 1L));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getRequestById_success() {
        when(itemRequestRepository.findByIdWithItems(100L)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.mapToRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = service.getRequestById(100L);

        assertEquals(100L, result.getId());
        assertEquals("need drill", result.getDescription());
        verify(itemRequestRepository).findByIdWithItems(100L);
        verify(itemRequestMapper).mapToRequestDto(itemRequest);
    }

    @Test
    void getRequestById_notFound_throws() {
        when(itemRequestRepository.findByIdWithItems(500L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getRequestById(500L));
    }

    @Test
    void deleteRequest_success() {
        when(itemRequestRepository.findByIdWithItems(100L)).thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));

        service.deleteRequest(100L, 1L);

        verify(itemRequestRepository).delete(itemRequest);
    }

    @Test
    void deleteRequest_notOwner_throwsValidationException() {
        User otherUser = User.builder().id(2L).name("other").email("other@mail.com").build();
        ItemRequest requestWithOtherOwner = ItemRequest.builder()
                .id(100L)
                .description("need drill")
                .requestor(otherUser)
                .created(LocalDateTime.now())
                .build();

        when(itemRequestRepository.findByIdWithItems(100L)).thenReturn(Optional.of(requestWithOtherOwner));
        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));

        assertThrows(ValidationException.class, () -> service.deleteRequest(100L, 1L));
        verify(itemRequestRepository, never()).delete(any());
    }
}