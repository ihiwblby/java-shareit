package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.Collection;

@Service
public interface BookingService {
    BookingResponseDto create(BookingCreationDto bookingCreationDto, Long userId);

    BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingResponseDto getById(Long userId, Long bookingId);

    Collection<BookingResponseDto> getByBooker(Long bookerId, BookingStatus status);

    Collection<BookingResponseDto> getByOwner(Long ownerId, BookingStatus status);
}
