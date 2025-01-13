package ru.practicum.shareit.booking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            var startField = value.getClass().getDeclaredField("start");
            var endField = value.getClass().getDeclaredField("end");

            startField.setAccessible(true);
            endField.setAccessible(true);

            var start = (LocalDateTime) startField.get(value);
            var end = (LocalDateTime) endField.get(value);

            if (start != null && end != null && start.isAfter(end)) {
                log.warn("Дата начала не может быть позже даты конца");
                return false;
            }

        } catch (Exception e) {
            log.error("Ошибка при валидации объекта");
            return false;
        }
        return true;
    }
}
