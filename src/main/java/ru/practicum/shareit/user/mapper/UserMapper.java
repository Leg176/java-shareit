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
        if (updateUserDto.hasName()) {
            user.setName(updateUserDto.getName().trim());
        }

        if (updateUserDto.hasEmail()) {
            user.setEmail(updateUserDto.getEmail().trim());
        }
        return user;
    }
}
