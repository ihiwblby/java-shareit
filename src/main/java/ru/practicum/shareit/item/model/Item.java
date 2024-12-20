package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"name", "description", "ownerId"})
@AllArgsConstructor
public class Item {
    Long id;
    String name;
    String description;
    Long ownerId;
    Boolean available;
    ItemRequest request;
}
