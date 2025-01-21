package ru.practicum.shareit.booking.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {

    BookingService bookingService;
    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto create(@RequestHeader(HEADER) Long userId,
                                     @RequestBody BookingCreateDto bookingCreateDto) {
        System.out.println("creating booking for user + " + userId + " item + " + bookingCreateDto.getItemId());
        var res = bookingService.create(bookingCreateDto, userId);
        System.out.println("ans itemid: " + res.getItem().getId() + ", bookerid" + res.getBooker().getId() + "status " + res.getStatus() + "end " + res.getEnd());
        return res;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(HEADER) Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader(HEADER) Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingResponseDto> getByBooker(@RequestHeader(HEADER) Long bookerId,
                                              @RequestParam(defaultValue = "ALL") BookingStatus state) {
        return bookingService.getByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getByOwner(@RequestHeader(HEADER) Long ownerId,
                                             @RequestParam(defaultValue = "ALL") BookingStatus state) {
        return bookingService.getByOwner(ownerId, state);
    }
}
