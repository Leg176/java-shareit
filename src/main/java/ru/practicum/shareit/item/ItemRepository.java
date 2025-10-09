package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Collection<Item> findAll();

    Collection<Item> getItemsUser(Long ownerId);

    Item create(Item item);

    Optional<Item> findByItemId(Long id);

    void delete(Long id);

    List<Item> searchAvailableFilms(String text);
}
