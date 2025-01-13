package ru.practicum.shareit.item.controller;

import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
public class ItemController {

    ItemService itemService;
    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@Validated(OnCreate.class) @RequestBody ItemDto itemDto,
                          @Positive @RequestHeader (HEADER) Long userId) {
        return itemService.create(itemDto, userId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@Positive @PathVariable Long id) {
        return itemService.getById(id);
    }

    @GetMapping
    public Collection<ItemDto> getAllByOwner(@Positive @RequestHeader(HEADER) Long userId) {
        return itemService.getAllByOwner(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Positive @PathVariable Long itemId,
                          @Validated(OnUpdate.class) @RequestBody ItemDto itemDto,
                          @Positive @RequestHeader(HEADER) Long userId) {
        return itemService.update(itemId, itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@Positive @PathVariable Long id) {
        itemService.delete(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Positive @PathVariable Long itemId,
                                    @Validated @RequestBody CommentDto commentDto,
                                    @Positive @RequestHeader(HEADER) Long userId) {
        return itemService.createComment(itemId, commentDto, userId);
    }
}