package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Collection<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Collection<Item> getItemsUser(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        if(item.getId() == null || item.getId() == 0) {
            Long id = getNextId();
            item.setId(id);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findByItemId(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> searchAvailableFilms(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String searchText = text.trim().toLowerCase();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item ->
                        item.getName().toLowerCase().contains(searchText) ||
                                (item.getDescription() != null &&
                                        item.getDescription().toLowerCase().contains(searchText))
                )
                .collect(Collectors.toList());
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
