package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    Optional<Item> getById(Long id);

    List<Item> getByOwnerId(Long userId);

    Item update(Long itemId,Item item);

    void delete(Long id);

    List<Item> search(String text);
}
