package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByEmail(String email);
}
