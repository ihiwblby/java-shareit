package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.validator.BookingDates;
import ru.practicum.shareit.booking.validator.TimeRange;

import java.time.LocalDateTime;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@BookingDates
public class BookingCreationDto implements TimeRange {
    @NotNull(message = "Дата начала бронирования не может быть null")
    LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    LocalDateTime end;

    @NotNull(message = "Предмет отзыва не может быть null")
    Long itemId;
}