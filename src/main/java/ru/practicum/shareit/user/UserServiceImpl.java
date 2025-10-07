package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
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

    @Override
    public Collection<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addNewUser(NewUserRequest request) {
        boolean isLogin = repository.findAll().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(request.getEmail()));
        if (isLogin) {
            throw new ValidationException("Пользователь с email: " + request.getEmail() + " существует");
        }
        User user = UserMapper.mapToUser(request);
        repository.create(user);
        return UserMapper.mapToUserDto(repository.create(user));
    }

    @Override
    public UserDto updateUser(UpdateUserRequest request) {
        isContainEmail(request);
        Long id = request.getId();
        User user = validationUser(id);
        UserMapper.updateUserFields(user, request);
        repository.create(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = validationUser(id);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        validationUser(id);
        repository.delete(id);
    }

    private void isContainEmail(UpdateUserRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            boolean isLogin = repository.findAll().stream()
                    .filter(user -> !user.getId().equals(request.getId()))
                    .map(User::getEmail)
                    .anyMatch(email -> email.equals(request.getEmail()));
            if (isLogin) {
                throw new ValidationException("Пользователь с email: " + request.getEmail() + " существует");
            }
        }
    }

    private User validationUser(Long id) {
        Optional<User> optUser = repository.findByUserId(id);
        if (optUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + id + " в базе отсутствует");
        }
        return optUser.get();
    }
}
