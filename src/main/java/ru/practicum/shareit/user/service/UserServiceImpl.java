package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        final User user = userRepository.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(Long id) {
        if (id <= 0) {
            throw new ConditionsNotMetException("ID пользователя должен быть больше 0");
        }

        final User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        if (userId <= 0) {
            throw new ConditionsNotMetException("ID пользователя должен быть больше 0");
        }

        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        final User updatedUser = userRepository.update(userId, UserMapper.toUser(userDto));
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        if (id <= 0) {
            throw new ConditionsNotMetException("ID пользователя должен быть больше 0");
        }
        userRepository.delete(id);
    }
}
