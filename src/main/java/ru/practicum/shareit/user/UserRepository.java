package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    // Вывод всех пользователей содержащихся в коллекции
    Collection<User> findAll();

    // Добавление нового пользователя в коллекцию
    User create(User user);

    // Вывод пользователя по его id
    Optional<User> findByUserId(Long id);

    // Удаление пользователя
    void delete(Long id);
}
