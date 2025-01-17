package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.validator.BookingDates;
import ru.practicum.shareit.booking.validator.TimeRange;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@BookingDates
public class BookingResponseDto implements TimeRange {
    Long id;

    @NotNull(message = "Дата начала бронирования не может быть null")
    LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    LocalDateTime end;

    @NotNull(message = "Предмет отзыва не может быть null")
    ItemDto item;

    @NotNull(message = "Пользователь, взявший в аренду, не может быть null")
    UserDto booker;

    @NotNull(message = "Статус бронирования не может быть null")
    BookingStatus status;

    @Override
    public LocalDateTime getStart() {
        return start;
    }

    @Override
    public LocalDateTime getEnd() {
        return end;
    }
}