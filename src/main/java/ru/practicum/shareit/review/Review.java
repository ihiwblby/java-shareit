package ru.practicum.shareit.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id", "text"})
public class Review {
    Long id;

    @NotBlank
    @Size(max = 500, message = "Отзыв не может быть длиннее 500 символов")
    String text;

    @NotNull(message = "Дата создания отзыва не может быть пустой")
    @PastOrPresent(message = "Дата создания отзыва не может быть в будущем")
    final LocalDate created;

    @NotNull(message = "Автор отзыва не может быть пустым")
    User author;

    @NotNull(message = "Предмет отзыва не может быть пустым")
    Item item;

    public Review() {
        this.created = LocalDate.now();
    }
}
