package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id", "description"})
public class ItemRequest {
    Long id;

    @NotBlank(message = "Текст запроса не может быть пустым")
    @Size(max = 200, message = "Текст запроса не может быть длиннее 200 символов")
    String description;

    @NotNull(message = "Пользователь, создавший запрос, не может быть пустым")
    User requester;

    @NotNull(message = "Дата создания запроса не может быть пустой")
    @PastOrPresent(message = "Дата создания запроса не может быть в будущем")
    final LocalDate created;

    public ItemRequest() {
        this.created = LocalDate.now();
    }
}
