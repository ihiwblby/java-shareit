package ru.practicum.shareit.item.dal;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRepositoryImpl implements ItemRepository {

    Map<Long, Item> items = new HashMap<>();

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

        if (!existingItem.getName().equals(item.getName()) && !StringUtils.isBlank(item.getName())) {
            existingItem.setName(item.getName());
        }
        if (!existingItem.getDescription().equals(item.getDescription()) && !StringUtils.isBlank(item.getDescription())) {
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
        if (StringUtils.isBlank(text)) {
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
            throw new DuplicateException("Эта вещь уже добавлена");
        }
    }
}
