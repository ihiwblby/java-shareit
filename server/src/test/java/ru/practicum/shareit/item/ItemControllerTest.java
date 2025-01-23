package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private ItemDto getItemDto() {
        return new ItemDto(1L, "Item Name", "Item Description", true, null, null, null, null);
    }

    private ItemCreateDto getItemCreateDto() {
        return new ItemCreateDto("Item Name", "Item Description", true, null);
    }

    private CommentDto getCommentDto() {
        return new CommentDto(1L, "Comment Text", 1L, "Author", LocalDateTime.now());
    }

    private CommentCreateDto getCommentCreateDto() {
        return new CommentCreateDto("Comment Text");
    }

    private ItemListResponseDto getItemListResponseDto() {
        return new ItemListResponseDto(1L, "Item Name", "Item Description", true);
    }

    @Test
    void createItemTest() throws Exception {
        ItemDto itemDto = getItemDto();
        ItemCreateDto itemCreateDto = getItemCreateDto();
        when(itemService.create(any(), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
        verify(itemService, times(1)).create(any(), anyLong());
    }

    @Test
    void getItemByIdTest() throws Exception {
        ItemDto itemDto = getItemDto();
        when(itemService.getById(anyLong())).thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
        verify(itemService, times(1)).getById(anyLong());
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        List<ItemListResponseDto> items = List.of(getItemListResponseDto());
        when(itemService.getAllByOwner(anyLong())).thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(items.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(items.getFirst().getName())))
                .andExpect(jsonPath("$[0].description", is(items.getFirst().getDescription())));
        verify(itemService, times(1)).getAllByOwner(anyLong());
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto itemDto = getItemDto();
        when(itemService.update(anyLong(), any(), anyLong())).thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
        verify(itemService, times(1)).update(anyLong(), any(), anyLong());
    }

    @Test
    void deleteItemTest() throws Exception {
        mvc.perform(delete("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1)).delete(anyLong());
    }

    @Test
    void searchItemsTest() throws Exception {
        List<ItemDto> items = List.of(getItemDto());
        when(itemService.search(any())).thenReturn(items);

        mvc.perform(get("/items/search")
                        .param("text", "query")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(items.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(items.getFirst().getName())))
                .andExpect(jsonPath("$[0].description", is(items.getFirst().getDescription())));
        verify(itemService, times(1)).search(any());
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDto commentDto = getCommentDto();
        CommentCreateDto commentCreateDto = getCommentCreateDto();
        when(itemService.createComment(anyLong(), any(), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
        verify(itemService, times(1)).createComment(anyLong(), any(), anyLong());
    }
}