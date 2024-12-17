package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User create(User user);
    Collection<User> getAll();
    Optional<User> getById(Long id);
    User update(Long userId, User user);
    void delete(Long id);
}
