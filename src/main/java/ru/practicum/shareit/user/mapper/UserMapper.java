package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User mapToUser(NewUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request не может быть пустым!");
        }
        return User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .build();
    }

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
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
