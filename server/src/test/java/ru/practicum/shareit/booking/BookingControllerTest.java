package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private static final String HEADER = "X-Sharer-User-Id";

    private BookingCreateDto getBookingCreateDto() {
        return new BookingCreateDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                1L);
    }

    private BookingResponseDto getBookingResponseDto() {
        UserDto userDto = new UserDto(1L, "user", "user@mail.ru");
        ItemDto itemDto = new ItemDto(1L, "item", "desc", true,0L,null,null,null);

        return BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBookingTest() throws Exception {
        BookingCreateDto bookingCreateDto = getBookingCreateDto();
        BookingResponseDto bookingResponseDto = getBookingResponseDto();
        when(bookingService.create(any(), anyLong())).thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().name())));
    }

    @Test
    void approveBookingTest() throws Exception {
        BookingResponseDto bookingResponseDto = getBookingResponseDto();
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/1")
                        .header(HEADER, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        BookingResponseDto bookingResponseDto = getBookingResponseDto();
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/1")
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().name())));
    }

    @Test
    void getBookingsByBookerTest() throws Exception {
        List<BookingResponseDto> bookings = List.of(getBookingResponseDto());
        when(bookingService.getByBooker(anyLong(), any())).thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header(HEADER, 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookings.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookings.getFirst().getStatus().name())));
    }

    @Test
    void getBookingsByOwnerTest() throws Exception {
        List<BookingResponseDto> bookings = List.of(getBookingResponseDto());
        when(bookingService.getByOwner(anyLong(), any())).thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header(HEADER, 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookings.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookings.getFirst().getStatus().name())));
    }
}