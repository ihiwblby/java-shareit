package ru.practicum.shareit.booking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.model.Booking;

@Slf4j
public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, Booking> {
    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext context) {
        if (booking == null) {
            return true;
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            log.warn("Дата начала бронирования не может быть позже даты конца бронирования");
            return false;
        }
        return true;
    }
}
