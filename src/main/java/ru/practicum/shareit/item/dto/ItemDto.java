package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import java.util.List;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id", "name", "description"})
@AllArgsConstructor
public class ItemDto {
    Long id;

    @NotBlank(message = "Название не может быть пустым", groups = OnCreate.class)
    String name;

    @NotBlank(message = "Описание не может быть пустым", groups = OnCreate.class)
    @Size(max = 200, message = "Описание не может быть длиннее 200 символов", groups = {OnCreate.class, OnUpdate.class})
    String description;

    @NotNull(message = "Должен быть указан статус бронирования", groups = OnCreate.class)
    Boolean available;

    ItemRequest request;

    BookingDto lastBooking;

    BookingDto nextBooking;

    List<CommentDto> comments;
}
