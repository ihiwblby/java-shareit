package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository;
    UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID = " + userId + " не найден"));

        final Item item = itemRepository.create(ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Long id) {
        return itemRepository.getById(id)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + id + " не найдена"));
    }

    @Override
    public Collection<ItemDto> getAllByOwner(Long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID = " + userId + " не найден"));

        return itemRepository.getByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID = " + userId + " не найден"));

        final Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + itemId + " не найдена"));

        if (!item.getOwnerId().equals(userId)) {
            throw new ConditionsNotMetException("Редактировать вещь может только её владелец");
        }

        final Item updatedItem = itemRepository.update(itemId, ItemMapper.toItem(itemDto, userId));

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public void delete(Long id) {
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