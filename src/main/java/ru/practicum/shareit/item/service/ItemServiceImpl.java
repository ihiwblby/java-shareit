package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    final ItemRepository itemRepository;
    final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        if (userId <= 0) {
            throw new ConditionsNotMetException("ID пользователя должен быть больше 0");
        }

        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID = " + userId + " не найден"));

        final Item item = itemRepository.create(ItemMapper.toItem(itemDto, userId));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Long id) {
        if (id <= 0) {
            throw new ConditionsNotMetException("ID вещи должен быть больше 0");
        }

        final Item item = itemRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + id + " не найдена"));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllByOwner(Long userId) {
        if (userId <= 0) {
            throw new ConditionsNotMetException("ID пользователя должен быть больше 0");
        }
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID = " + userId + " не найден"));

        return itemRepository.getByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        if (userId <= 0) {
            throw new ConditionsNotMetException("ID пользователя должен быть больше 0");
        }
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID = " + userId + " не найден"));

        if (itemId <= 0) {
            throw new ConditionsNotMetException("ID вещи должен быть больше 0");
        }
        final Item item =itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + itemId + " не найдена"));

        if (!item.getOwnerId().equals(userId)) {
            throw new ConditionsNotMetException("Редактировать вещь может только её владелец");
        }

        final Item updatedItem = itemRepository.update(itemId, ItemMapper.toItem(itemDto, userId));

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public void delete(Long id) {
        if (id <= 0) {
            throw new ConditionsNotMetException("ID вещи должен быть больше 0");
        }

        itemRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + id + " не найдена"));

        itemRepository.delete(id);
    }

    @Override
    public Collection<ItemDto> search(String text) {
        String normalizedText = text.toLowerCase();

        return itemRepository.search(normalizedText)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
