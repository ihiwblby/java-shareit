package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListResponseDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto getById(Long id);

    List<ItemListResponseDto> getAllByOwner(Long userId);

    ItemDto update(Long itemId, ItemDto itemDto, Long userId);

    void delete(Long id);

    Collection<ItemDto> search(String text);

    CommentDto createComment(Long itemId, CommentDto commentDto, Long userId);
}
