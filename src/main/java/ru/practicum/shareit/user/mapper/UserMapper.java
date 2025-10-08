package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public User mapToUser(NewUserDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Request не может быть пустым!");
        }
        return User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .build();
    }

    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User updateUserFields(User user, UpdateUserDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Request не может быть пустым!");
        }
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        return user;
    }
}
