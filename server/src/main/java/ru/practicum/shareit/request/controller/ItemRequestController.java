package ru.practicum.shareit.request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(path = "/requests")
public class ItemRequestController {
    ItemRequestService requestService;
    static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.create(itemRequestCreateDto, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllForUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getAllForUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAll() {
        return requestService.getAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PathVariable Long requestId) {
        return requestService.getById(userId, requestId);
    }
}