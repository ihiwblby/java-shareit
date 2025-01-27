package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.validator.BookingDates;
import ru.practicum.shareit.booking.validator.TimeRange;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@BookingDates
public class BookingCreateDto implements TimeRange {
    @NotNull(message = "Дата начала бронирования не может быть null")
    LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    LocalDateTime end;

    @NotNull(message = "Предмет отзыва не может быть null")
    Long itemId;

    @Override
    public LocalDateTime getStart() {
        return start;
    }

    @Override
    public LocalDateTime getEnd() {
        return end;
    }
}