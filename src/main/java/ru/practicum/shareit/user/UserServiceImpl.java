package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addNewUser(NewUserDto request) {
        isContainsEmail(request.getEmail(), null);
        User user = userMapper.mapToUser(request);
        repository.create(user);
        return userMapper.mapToUserDto(repository.create(user));
    }

    @Override
    public UserDto updateUser(UpdateUserDto request) {
        if (request.hasEmail()) {
            isContainsEmail(request.getEmail(), request.getId());
        }
        Long id = request.getId();
        User user = findByIdUser(id);
        userMapper.updateUserFields(request, user);
        repository.create(user);
        return userMapper.mapToUserDto(user);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = findByIdUser(id);
        return userMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        findByIdUser(id);
        repository.delete(id);
    }

    private void isContainsEmail(String newEmail, Long id) {
        boolean emailExists = repository.findAll().stream()
                .filter(user -> id == null || !user.getId().equals(id))
                .map(User::getEmail)
                .anyMatch(email -> email.equals(newEmail));
        if (emailExists) {
            throw new ValidationException("Пользователь с email: " + newEmail + " существует");
        }
    }

    private User findByIdUser(Long id) {
        Optional<User> optUser = repository.findByUserId(id);
        if (optUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + id + " в базе отсутствует");
        }
        return optUser.get();
    }
}
