package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemCreateDto itemCreateDto, Long userId) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> getItem(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> updateItem(Long itemId, ItemDto itemDto, Long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public void deleteItem(Long itemId) {
        delete("/" + itemId);
    }

    public ResponseEntity<Object> createComment(Long itemId, CommentCreateDto commentCreateDto, Long userId) {
        return post("/" + itemId + "/comment", userId, commentCreateDto);
    }
}
