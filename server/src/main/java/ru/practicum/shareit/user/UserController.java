package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto create(@RequestBody NewUserDto userRequest) {
        return userService.saveUser(userRequest);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody UpdateUserDto request) {
        request.setId(userId);
        return userService.updateUser(request);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
