package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public Collection<ItemRequestDto> getRequests() {
        return itemRequestRepository.findAll().stream()
                .map(itemRequestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getRequestsByOwner(Long ownerId) {
        findByIdUser(ownerId);
        return itemRequestRepository.getRequestsUser(ownerId).stream()
                .map(itemRequestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto addNewRequest(NewItemRequestDto newRequest, Long ownerId) {
        User user = findByIdUser(ownerId);
        ItemRequest request = itemRequestMapper.mapToRequest(newRequest, user);
        return itemRequestMapper.mapToRequestDto(request);
    }

    @Override
    public ItemRequestDto updateRequest(UpdateItemRequestDto updateRequest, Long ownerId) {
        ItemRequest request = findByIdItemRequest(updateRequest.getId());
        findByIdUser(ownerId);
        validationOwner(request, ownerId);
        itemRequestMapper.updateRequestFields(request, updateRequest);
        itemRequestRepository.create(request);
        return itemRequestMapper.mapToRequestDto(request);
    }

    @Override
    public ItemRequestDto getRequestById(Long id) {
        ItemRequest request = findByIdItemRequest(id);
        return itemRequestMapper.mapToRequestDto(request);
    }

    @Override
    public void deleteRequest(Long id, Long ownerId) {
        ItemRequest request = findByIdItemRequest(id);
        findByIdUser(ownerId);
        validationOwner(request, ownerId);
        itemRequestRepository.delete(id);
    }

    private User findByIdUser(Long id) {
        Optional<User> optUser = userRepository.findByUserId(id);
        if (optUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + id + " в базе отсутствует");
        }
        return optUser.get();
    }

    private ItemRequest findByIdItemRequest(Long id) {
        Optional<ItemRequest> optRequest = itemRequestRepository.findByRequestId(id);
        if (optRequest.isEmpty()) {
            throw new NotFoundException("Запрос с id: " + id + " в базе отсутствует");
        }
        return optRequest.get();
    }

    private void validationOwner(ItemRequest request, Long ownerId) {
        if (!request.getRequestor().getId().equals(ownerId)) {
            throw new ValidationException("Вы не являетесь владельцем, доступ запрещён!");
        }
    }
}

