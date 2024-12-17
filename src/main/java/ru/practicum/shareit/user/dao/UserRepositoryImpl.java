package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        isDuplicate(user);
        final Long id = getNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User update(Long userId, User user) {
        isDuplicate(user);
        final User existingUser = users.get(userId);
        if (!existingUser.getName().equals(user.getName()) && user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (!existingUser.getEmail().equals(user.getEmail()) && user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        users.put(userId, existingUser);
        return users.get(userId);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void isDuplicate(User user) {
        boolean isDuplicate = users.values()
                .stream()
                .anyMatch(existingUser -> existingUser.equals(user));
        if (isDuplicate) {
            throw new DuplicateException("Пользователь с таким email уже создан");
        }
    }
}