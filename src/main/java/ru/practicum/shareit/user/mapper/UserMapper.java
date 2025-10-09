package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mapToUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User mapToUser(NewUserDto newUserDto);

    default User updateUserFields(UpdateUserDto updateUserDto, @MappingTarget User user) {
        if (updateUserDto == null) {
            return user;
        }

        if (isNotBlank(updateUserDto.getName())) {
            user.setName(updateUserDto.getName().trim());
        }

        if (isNotBlank(updateUserDto.getEmail())) {
            user.setEmail(updateUserDto.getEmail().trim());
        }
        return user;
    }

    default boolean isNotBlank(String value) {
        return value != null && !value.isEmpty();
    }
}
