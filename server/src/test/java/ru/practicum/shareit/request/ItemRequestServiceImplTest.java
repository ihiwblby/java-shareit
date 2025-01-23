package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final ItemRequestServiceImpl itemRequestService;
    private final UserService userService;

    UserDto userDto;
    UserDto userDto2;

    @BeforeEach
    public void setUp() {
        userDto = userService.create(new UserDto(1L, "Иван", "example1@mail.com"));
        userDto2 = userService.create(new UserDto(2L,"Мария", "example2@mail.com"));
    }

    @AfterEach
    public void tearDown() {
        userService.delete(userDto.getId());
        userService.delete(userDto2.getId());
    }

    @Test
    void createItemRequestTest() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a tent");
        ItemRequestDto createdRequest = itemRequestService.create(requestDto, userDto.getId());

        assertNotNull(createdRequest);
        assertEquals("Need a tent", createdRequest.getDescription());
        assertEquals(userDto.getId(), createdRequest.getRequester().getId());
    }

    @Test
    void createItemRequestInvalidUserTest() {
        Long invalidUserId = 999L;
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a sleeping bag");
        assertThrows(NotFoundException.class, () -> itemRequestService.create(requestDto, invalidUserId));
    }

    @Test
    void getAllForUserTest() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a cup");
        ItemRequestDto createdRequest = itemRequestService.create(requestDto, userDto.getId());

        List<ItemRequestResponseDto> userRequests = itemRequestService.getAllForUser(userDto.getId());
        assertNotNull(userRequests);
        assertTrue(userRequests.size() == 1);
    }

    @Test
    void getAllForUserInvalidUserTest() {
        Long invalidUserId = 999L;
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllForUser(invalidUserId));
    }

    @Test
    void getAllTest() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a plate");
        ItemRequestDto createdRequest = itemRequestService.create(requestDto, userDto.getId());

        ItemRequestCreateDto requestSecondDto = new ItemRequestCreateDto("Need a fork");
        ItemRequestDto createdSecondRequest = itemRequestService.create(requestSecondDto, userDto2.getId());
        List<ItemRequestResponseDto> allRequests = itemRequestService.getAll();

        assertNotNull(allRequests);
        assertTrue(allRequests.size() == 2);
    }

    @Test
    void getByIdTest() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a plate");
        ItemRequestDto createdRequest = itemRequestService.create(requestDto, userDto.getId());

        ItemRequestResponseDto request = itemRequestService.getById(userDto.getId(), createdRequest.getId());

        assertNotNull(request);
        assertEquals(createdRequest.getId(), request.getId());
    }
}