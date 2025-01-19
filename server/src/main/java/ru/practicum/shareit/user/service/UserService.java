package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserCreateDto;

public interface UserService {
    UserDto create(UserCreateDto userCreateDto);

    UserDto getById(Long id);

    UserDto update(Long userId, UserDto userDto);

    void delete(Long id);
}