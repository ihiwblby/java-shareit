package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto create(BookingCreationDto bookingCreationDto, Long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));

        Item item = itemRepository.findById(bookingCreationDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + bookingCreationDto.getItemId() + " не найдена"));

        if (item.getOwner().equals(booker)) {
            throw new AccessDeniedException("Владелец вещи не может бронировать свою вещь");
        }
        if (!item.getAvailable()) {
            throw new ConditionsNotMetException("Вещь не доступна для бронирования");
        }

        Booking booking = BookingMapper.toBooking(bookingCreationDto, item, booker);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID = " + bookingId + " не найдено"));

        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Только владелец вещи может подтверждать бронирование");
        }

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID = " + bookingId + " не найдено"));

        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new AccessDeniedException("Только владелец или арендатор могут просматривать бронирование");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getByBooker(Long bookerId, BookingStatus status) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + bookerId + " не найден"));

        List<Booking> bookings;

        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(booker, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndEndBefore(
                        booker, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndStartAfter(
                        booker, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(
                        booker, LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingRepository.findAllByBookerAndStatus(
                        booker, status, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                throw new NotFoundException("Некорректное состояние бронирования");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getByOwner(Long ownerId, BookingStatus status) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + ownerId + " не найден"));

        List<Booking> bookings;

        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwner(owner, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerAndEndBefore(
                        owner, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerAndStartAfter(
                        owner, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(
                        owner, LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerAndStatus(
                        owner, status, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                throw new NotFoundException("Некорректное состояние бронирования");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}
