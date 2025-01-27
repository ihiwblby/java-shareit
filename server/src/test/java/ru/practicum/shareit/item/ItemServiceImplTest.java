package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    UserDto userDto1;
    UserDto userDto2;

    ItemCreateDto itemCreateDto1 = new ItemCreateDto("Палатка", "Большая палатка для кемпинга", false, null);
    ItemCreateDto itemCreateDto2 = new ItemCreateDto("Перфоратор", "Мощный перфоратор для сверления", true, null);
    ItemCreateDto itemCreateDto3 = new ItemCreateDto("Роман Анна Каренина", "Книга Анна Каренина", true, null);

    ItemDto itemDto1;
    ItemDto itemDto2;
    ItemDto itemDto3;

    @BeforeEach
    void setUp() {
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
    void createTest() {
        ItemCreateDto newItem = ItemCreateDto.builder()
                .name("Ноутбук")
                .description("Ноутбук хороший")
                .available(true)
                .build();
        ItemDto createdItem = itemService.create(newItem, userDto1.getId());
        assertEquals(newItem.getName(), createdItem.getName());
        assertEquals(newItem.getDescription(), createdItem.getDescription());
    }

    @Test
    void getByIdTest() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("Charger", "xiaomi", true, null);
        ItemDto item = itemService.create(itemCreateDto, userDto2.getId());

        BookingCreateDto bookingDto = BookingCreateDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .itemId(itemDto2.getId())
                .build();
        BookingResponseDto createdBooking = bookingService.create(bookingDto, userDto1.getId());

        ItemDto itemDto = itemService.getById(item.getId());
        assertEquals(item.getId(), itemDto.getId());
    }

    @Test
    void getAllByOwnerTest() {
        List<ItemListResponseDto> items = itemService.getAllByOwner(userDto2.getId());
        assertEquals(items.size(), 2);
    }

    @Test
    void updateTest() {
        itemDto1.setName("Laptop Windows");
        ItemDto itemDtoUpdate = itemService.update(itemDto1.getId(), itemDto1, userDto1.getId());
        assertEquals(itemDto1, itemDtoUpdate);
    }

    @Test
    void deleteTest() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("laptop", "xiaomi", true, null);
        ItemDto item = itemService.create(itemCreateDto, userDto2.getId());

        itemService.delete(item.getId());
        assertThrows(NotFoundException.class, () -> {
            itemService.getById(item.getId());
        });
    }

    @Test
    void searchTest() {
        Collection<ItemDto> listBySearch = itemService.search("Перфоратор");
        assertEquals(listBySearch.size(), 1);
    }

    @Test
    void createCommentTest() {
        CommentCreateDto newComment = CommentCreateDto.builder()
                .text("cool palatka")
                .build();

        ItemCreateDto itemCreateDto = new ItemCreateDto("Палатка", "Большая палатка для кемпинга", true, null);
        ItemDto item = itemService.create(itemCreateDto, userDto1.getId());

        UserDto booker = userService.create(new UserDto(100L, "Мария", "mary@email.com"));

        BookingCreateDto bookingDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .itemId(item.getId())
                .build();
        BookingResponseDto createdBooking = bookingService.create(bookingDto, booker.getId());
        bookingService.approve(userDto1.getId(), createdBooking.getId(), true);

        CommentDto createdComment = itemService.createComment(item.getId(), newComment, booker.getId());
        assertEquals(newComment.getText(), createdComment.getText());
    }

    @Test
    void updateItemNotValidOwnerTest() {
        itemDto1.setName("AnyName");
        assertThrows(ConditionsNotMetException.class, () -> {
            itemService.update(itemDto1.getId(),itemDto1, userDto2.getId());
        });
    }
}