package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.getUsers();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody @NotNull NewUserRequest userRequest) {
        return userService.addNewUser(userRequest);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable @Positive(message = "id должен быть больше 0") Long userId,
                           @Valid @RequestBody @NotNull UpdateUserRequest request) {
        request.setId(userId);
        return userService.updateUser(request);
    }


    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        userService.deleteUser(id);
    }
}
