package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);
    ItemDto getById(Long id);
    Collection<ItemDto> getAllByOwner(Long userId);
    ItemDto update(Long itemId, ItemDto itemDto, Long userId);
    void delete(Long id);
    Collection<ItemDto> search(String text);
}
