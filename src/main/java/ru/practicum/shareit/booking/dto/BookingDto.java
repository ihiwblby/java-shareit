package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.validator.TimeRange;

import java.time.LocalDateTime;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "start", "end"})
public class BookingDto implements TimeRange {
    Long id;

    @NotNull(message = "Дата начала бронирования не может быть null")
    LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    LocalDateTime end;

    @NotNull(message = "Предмет отзыва не может быть null")
    Long itemId;

    @NotNull(message = "Пользователь, взявший в аренду, не может быть null")
    Long bookerId;

    @NotNull(message = "Статус не может быть null")
    BookingStatus status;
}
