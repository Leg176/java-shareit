package ru.practicum.shareit.request;

import ru.practicum.shareit.request.model.Request;
import java.util.Collection;
import java.util.Optional;

public interface RequestRepository {
    Collection<Request> findAll();

    Collection<Request> getRequestsUser(Long ownerId);

    Request create(Request request);

    Optional<Request> findByRequestId(Long id);

    void delete(Long id);
}
