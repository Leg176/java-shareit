package ru.practicum.shareit.request;

import ru.practicum.shareit.request.model.ItemRequest;
import java.util.Collection;
import java.util.Optional;

public interface ItemRequestRepository {
    Collection<ItemRequest> findAll();

    Collection<ItemRequest> getRequestsUser(Long ownerId);

    ItemRequest create(ItemRequest request);

    Optional<ItemRequest> findByRequestId(Long id);

    void delete(Long id);
}
