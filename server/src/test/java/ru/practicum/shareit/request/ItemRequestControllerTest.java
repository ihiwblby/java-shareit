package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    private static final String HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private ItemRequestDto getItemRequestDto() {
        return new ItemRequestDto(1L, "Request description", null,
                LocalDateTime.now(), null);
    }

    private ItemRequestResponseDto getItemRequestResponseDto() {
        return new ItemRequestResponseDto(1L, "Request description",
                LocalDateTime.now(), null);
    }

    private ItemRequestCreateDto getItemRequestCreateDto() {
        return new ItemRequestCreateDto("Request description");
    }

    @Test
    void createTest() throws Exception {
        ItemRequestDto itemRequestDto = getItemRequestDto();
        ItemRequestCreateDto itemRequestCreateDto = getItemRequestCreateDto();
        when(requestService.create(any(), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
        verify(requestService, times(1)).create(any(), anyLong());
    }

    @Test
    void getAllForUserTest() throws Exception {
        List<ItemRequestResponseDto> responses = List.of(getItemRequestResponseDto());
        when(requestService.getAllForUser(anyLong())).thenReturn(responses);

        mvc.perform(get("/requests")
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responses.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responses.getFirst().getDescription())));
        verify(requestService, times(1)).getAllForUser(anyLong());
    }

    @Test
    void getAllTest() throws Exception {
        List<ItemRequestResponseDto> responses = List.of(getItemRequestResponseDto());
        when(requestService.getAll()).thenReturn(responses);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responses.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responses.getFirst().getDescription())));
        verify(requestService, times(1)).getAll();
    }

    @Test
    void getByIdTest() throws Exception {
        ItemRequestResponseDto response = getItemRequestResponseDto();
        when(requestService.getById(anyLong(), anyLong())).thenReturn(response);

        mvc.perform(get("/requests/1")
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(response.getDescription())));
        verify(requestService, times(1)).getById(anyLong(), anyLong());
    }
}