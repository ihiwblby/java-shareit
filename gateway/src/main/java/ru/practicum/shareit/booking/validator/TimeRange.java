package ru.practicum.shareit.booking.validator;

import java.time.LocalDateTime;

public interface TimeRange {
    LocalDateTime getStart();

    LocalDateTime getEnd();
}
