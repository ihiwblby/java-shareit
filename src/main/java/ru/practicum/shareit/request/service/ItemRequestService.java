package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestResponseDto> getAllForUser(Long userId);

    List<ItemRequestResponseDto> getAll();

    ItemRequestResponseDto getById(Long userId, Long requestId);
}
