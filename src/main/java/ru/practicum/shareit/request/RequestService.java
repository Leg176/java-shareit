package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;
import java.util.Collection;

public interface RequestService {
    Collection<RequestDto> getRequests();

    Collection<RequestDto> getRequestsByOwner(Long ownerId);

    RequestDto addNewRequest(NewRequest request, Long ownerId);

    RequestDto updateRequest(UpdateRequest request, Long ownerId);

    RequestDto getRequestById(Long id);

    void deleteRequest(Long id, Long ownerId);
}
