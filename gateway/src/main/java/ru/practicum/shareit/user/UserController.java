package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable @Positive(message = "id должен быть больше 0") Long id) {
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody @NotNull NewUserDto request) {
        return userClient.saveUser(request);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @Positive(message = "id должен быть больше 0") Long userId,
                                         @Valid @RequestBody @NotNull UpdateUserDto request) {
        return userClient.updateUser(request, userId);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Object> deleteUser(@PathVariable("id") @Positive Long id) {
        return userClient.deleteUser(id);
    }
}
