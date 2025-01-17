package ru.practicum.shareit.booking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class BookingDatesValidator implements ConstraintValidator<BookingDates, TimeRange> {
    @Override
    public boolean isValid(TimeRange timeRange, ConstraintValidatorContext context) {
        if (timeRange == null) {
            return true;
        }

        final LocalDateTime start = timeRange.getStart();
        final LocalDateTime end = timeRange.getEnd();

        if (start.isAfter(end)) {
            log.warn("Дата начала бронирования не может быть позже даты конца бронирования");
            return false;
        }

        if (start.equals(end)) {
            log.warn("Дата начала бронирования не может быть равна дате окончания бронирования");
            return false;
        }

        return true;
    }
}
