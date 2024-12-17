package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"email"})
@AllArgsConstructor
public class UserDto {
    Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым", groups = OnCreate.class)
    @Size(max = 50, message = "Имя не может быть длиннее 50 символов", groups = {OnCreate.class, OnUpdate.class})
    String name;

    @NotBlank(message = "email пользователя не может быть пустым", groups = OnCreate.class)
    @Email(message = "email введён некорректно", groups = {OnCreate.class, OnUpdate.class})
    String email;
}
