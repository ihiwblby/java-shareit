package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class BookingServiceImplTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    ItemCreateDto itemCreateDto1 = new ItemCreateDto("Палатка", "Большая палатка для кемпинга", false, null);
    ItemCreateDto itemCreateDto2 = new ItemCreateDto("Перфоратор", "Мощный перфоратор для сверления", true, null);
    ItemCreateDto itemCreateDto3 = new ItemCreateDto("Роман Анна Каренина", "Книга Анна Каренина", true, null);

    ItemDto itemDto1;
    ItemDto itemDto2;
    ItemDto itemDto3;

    UserDto userDto1;
    UserDto userDto2;

    @BeforeEach
    public void setUp() {
        userDto1 = userService.create(new UserDto(1L, "Иван", "example1@mail.com"));
        userDto2 = userService.create(new UserDto(2L,"Мария", "example2@mail.com"));

        itemDto1 = itemService.create(itemCreateDto1, userDto1.getId());
        itemDto2 = itemService.create(itemCreateDto2, userDto2.getId());
        itemDto3 = itemService.create(itemCreateDto3, userDto2.getId());
    }

    @AfterEach
    public void tearDown() {
        itemService.delete(itemDto1.getId());
        itemService.delete(itemDto2.getId());
        itemService.delete(itemDto3.getId());

        userService.delete(userDto1.getId());
        userService.delete(userDto2.getId());
    }

    @Test
    void createBookingTest() {
        UserDto booker = userDto1;
        ItemDto item = itemDto3;
        BookingCreateDto bookingDto = BookingCreateDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .itemId(itemDto3.getId())
                .build();
        BookingResponseDto createdBooking = bookingService.create(bookingDto, booker.getId());
        assertEquals(bookingDto.getStart(), createdBooking.getStart());
        assertEquals(bookingDto.getEnd(), createdBooking.getEnd());
        assertEquals(bookingDto.getItemId(), createdBooking.getItem().getId());
    }

    @Test
    void approveTest() {
        UserDto owner = userService.create(new UserDto(10L, "Анна", "anna@email.com"));
        UserDto booker = userService.create(new UserDto(11L, "Михаил", "michael@email.com"));
        ItemCreateDto itemCreateDto = new ItemCreateDto("Палатка", "Большая палатка для кемпинга", true, null);
        ItemDto item = itemService.create(itemCreateDto, owner.getId());

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(item.getId())
                .build();

        BookingResponseDto createdBooking = bookingService.create(bookingCreateDto, booker.getId());
        BookingResponseDto approvedBooking = bookingService.approve(owner.getId(), createdBooking.getId(), true);

        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
        assertEquals(createdBooking.getId(), approvedBooking.getId());
        assertEquals(booker, approvedBooking.getBooker());
    }

    @Test
    void getByIdTest() {
        UserDto owner = userService.create(new UserDto(20L, "Ирина", "irina@email.com"));
        UserDto booker = userService.create(new UserDto(21L, "Александр", "alex@email.com"));
        ItemCreateDto itemCreateDto = new ItemCreateDto("Charger", "xiaomi", true, null);
        ItemDto item = itemService.create(itemCreateDto, owner.getId());

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(item.getId())
                .build();

        BookingResponseDto createdBooking = bookingService.create(bookingCreateDto, booker.getId());

        BookingResponseDto foundBooking = bookingService.getById(booker.getId(), createdBooking.getId());

        assertEquals(createdBooking.getId(), foundBooking.getId());
        assertEquals(createdBooking.getItem().getId(), foundBooking.getItem().getId());
        assertEquals(createdBooking.getBooker().getId(), foundBooking.getBooker().getId());
    }

    @Test
    void getByBookerTest() {
        UserDto booker = userDto1;
        ItemDto item = itemDto2;

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .itemId(item.getId())
                .build();

        BookingResponseDto createdBooking = bookingService.create(bookingCreateDto, booker.getId());

        Collection<BookingResponseDto> bookings = bookingService.getByBooker(booker.getId(), BookingStatus.WAITING);

        assertEquals(1, bookings.size());

        BookingResponseDto booking = bookings.stream().findFirst().get();

        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
    }

    @Test
    void getByOwnerTest() {
        UserDto owner = userDto2;
        UserDto booker = userDto1;
        ItemDto item = itemDto2;

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();

        BookingResponseDto createdBooking = bookingService.create(bookingCreateDto, booker.getId());

        Collection<BookingResponseDto> bookings = bookingService.getByOwner(owner.getId(), BookingStatus.WAITING);
        BookingResponseDto booking = bookings.stream().findFirst().get();

        assertEquals(1, bookings.size());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
    }
}
