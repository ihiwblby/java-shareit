package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        isDuplicate(item);
        final Long id = getNextId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getByOwnerId(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public Item update(Long itemId, Item item) {
        isDuplicate(item);
        final Item existingItem = items.get(itemId);
        if (!existingItem.getName().equals(item.getName()) && item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (!existingItem.getDescription().equals(item.getDescription()) && item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (!existingItem.getAvailable().equals(item.getAvailable()) && item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, existingItem);
        return items.get(itemId);
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                .toList();
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void isDuplicate(Item item) {
        boolean isDuplicate = items.values()
                .stream()
                .anyMatch(existingUser -> existingUser.equals(item));
        if (isDuplicate) {
            throw new ConditionsNotMetException("Эта вещь уже добавлена");
        }
    }
}
