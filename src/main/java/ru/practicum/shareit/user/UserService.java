package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.Collection;

public interface UserService {
    // Вывод всех пользователей содержащихся в коллекции
    Collection<UserDto> getUsers();

    // Добавление нового пользователя в коллекцию
    UserDto addNewUser(NewUserRequest request);

    // Обновления данных о пользователе в коллекции
    UserDto updateUser(UpdateUserRequest request);

    // Вывод пользователя по его id
    UserDto getUserById(Long id);

    // Удаление пользователя
    void deleteUser(Long id);
}
