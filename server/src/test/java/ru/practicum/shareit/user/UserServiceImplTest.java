package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class UserServiceImplTest {

    private final UserService userService;

    UserDto newUser = new UserDto(200L, "Петр", "example2@mail.com");

    @Test
    void createTest() {
        UserDto createdUser = userService.create(newUser);
        assertEquals(createdUser, userService.getById(createdUser.getId()));
    }

    @Test
    void getByIdTest() {
        UserDto createdUser = userService.create(newUser);
        UserDto foundUser = userService.getById(createdUser.getId());
        assertEquals(foundUser.getId(), createdUser.getId());
    }

    @Test
    void updateTest() {
        UserDto newUser = new UserDto(220L, "Иван", "ivan@mail.com");
        UserDto createdUser = userService.create(newUser);
        createdUser.setName("UpdatedUser");
        UserDto updatedUser = userService.update(createdUser.getId(), createdUser);
        assertEquals(createdUser.getName(), updatedUser.getName());
    }

    @Test
    void deleteTest() {
        UserDto createdUser = userService.create(newUser);
        userService.delete(createdUser.getId());
        assertThrows(NotFoundException.class, () -> {
            userService.getById(createdUser.getId());
        });
    }
}