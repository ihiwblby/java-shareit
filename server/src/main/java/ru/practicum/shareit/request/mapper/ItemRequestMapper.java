package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .created(itemRequest.getCreated())
                .items(ItemMapper.toItemForItemRequestDtoList(itemRequest.getItems() != null
                        ? itemRequest.getItems() : Collections.emptyList()))
                .build();
    }

    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }

        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(ItemMapper.toItemForItemRequestDtoList(itemRequest.getItems() != null
                        ? itemRequest.getItems() : Collections.emptyList()))
                .build();
    }

    public static List<ItemRequestResponseDto> toItemRequestResponseDtoList(List<ItemRequest> itemRequests) {
        if (itemRequests == null) {
            return Collections.emptyList();
        }
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .toList();
    }
}