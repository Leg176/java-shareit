package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRequestRepositoryImpl implements ItemRequestRepository {
    private final Map<Long, ItemRequest> requests = new HashMap<>();

    @Override
    public Collection<ItemRequest> findAll() {
        return new ArrayList<>(requests.values());
    }

    @Override
    public Collection<ItemRequest> getRequestsUser(Long ownerId) {
        return requests.values().stream()
                .filter(request -> request.getRequestor().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequest create(ItemRequest request) {
        if (request.getId() == null || request.getId() == 0) {
            Long id = getNextId();
            request.setId(id);
        }
        requests.put(request.getId(), request);
        return request;
    }

    @Override
    public Optional<ItemRequest> findByRequestId(Long id) {
        return Optional.ofNullable(requests.get(id));
    }

    @Override
    public void delete(Long id) {
        requests.remove(id);
    }

    private long getNextId() {
        long currentMaxId = requests.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
