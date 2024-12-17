package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.validator.StartBeforeEnd;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id", "start", "end"})
@StartBeforeEnd
public class Booking {
    Long id;

    @NotNull(message = "Дата начала бронирования не может быть null")
    @PastOrPresent(message = "Дата начала бронирования не может быть в будущем")
    LocalDate start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    @PastOrPresent(message = "Дата окончания бронирования не может быть в будущем")
    LocalDate end;

    @NotNull(message = "Предмет отзыва не может быть null")
    Item item;

    @NotNull(message = "Пользователь, взявший в аренду, не может быть null")
    User booker;

    @NotNull(message = "Status не может быть null")
    BookingStatus status;
}
