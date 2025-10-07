package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<RequestDto> getRequests() {
        return requestRepository.findAll().stream()
                .map(RequestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<RequestDto> getRequestsByOwner(Long ownerId) {
        validationUser(ownerId);
        return requestRepository.getRequestsUser(ownerId).stream()
                .map(RequestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto addNewRequest(NewRequest newRequest, Long ownerId) {
        User user = validationUser(ownerId);
        Request request = RequestMapper.mapToRequest(newRequest, user);
        return RequestMapper.mapToRequestDto(request);
    }

    @Override
    public RequestDto updateRequest(UpdateRequest updateRequest, Long ownerId) {
        Request request = validationRequest(updateRequest.getId());
        validationUser(ownerId);
        validationOwner(request, ownerId);
        RequestMapper.updateRequestFields(request, updateRequest);
        requestRepository.create(request);
        return RequestMapper.mapToRequestDto(request);
    }

    @Override
    public RequestDto getRequestById(Long id) {
        Request request = validationRequest(id);
        return RequestMapper.mapToRequestDto(request);
    }

    @Override
    public void deleteRequest(Long id, Long ownerId) {
        Request request = validationRequest(id);
        validationUser(ownerId);
        validationOwner(request, ownerId);
        requestRepository.delete(id);
    }

    private User validationUser(Long id) {
        Optional<User> optUser = userRepository.findByUserId(id);
        if (optUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + id + " в базе отсутствует");
        }
        return optUser.get();
    }

    private Request validationRequest(Long id) {
        Optional<Request> optRequest = requestRepository.findByRequestId(id);
        if (optRequest.isEmpty()) {
            throw new NotFoundException("Запрос с id: " + id + " в базе отсутствует");
        }
        return optRequest.get();
    }

    private void validationOwner(Request request, Long ownerId) {
        if (!request.getRequestor().getId().equals(ownerId)) {
            throw new ValidationException("Вы не являетесь владельцем, доступ запрещён!");
        }
    }
}

