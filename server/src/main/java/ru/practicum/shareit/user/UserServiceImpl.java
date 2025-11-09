package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<User> users = repository.findAll();
        return users.stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto saveUser(NewUserDto request) {
        isContainsEmail(request.getEmail(), null);
        User user = userMapper.mapToUser(request);
        repository.save(user);
        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UpdateUserDto request) {
        if (request.hasEmail()) {
            isContainsEmail(request.getEmail(), request.getId());
        }
        Long id = request.getId();
        User user = findByIdUser(id);
        userMapper.updateUserFields(request, user);
        repository.save(user);
        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = findByIdUser(id);
        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findByIdUser(id);
        repository.delete(user);
    }

    private void isContainsEmail(String newEmail, Long id) {
        boolean emailExists;
        if (id == null) {
            emailExists = repository.existsByEmail(newEmail);
        } else {
            emailExists = repository.existsByEmailAndIdNot(newEmail, id);
        }
        if (emailExists) {
            throw new ValidationException("Пользователь с email: " + newEmail + " существует");
        }
    }

    private User findByIdUser(Long id) {
        Optional<User> optUser = repository.findById(id);
        if (optUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + id + " в базе отсутствует");
        }
        return optUser.get();
    }
}
