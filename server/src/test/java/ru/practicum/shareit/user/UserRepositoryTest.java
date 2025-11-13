package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.entity.User;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void save_findAll_findById() {
        User userOne = userRepository.save(User.builder()
                .name("john")
                .email("j@d.com")
                .build()
        );
        User userTwo = userRepository.save(User.builder()
                .name("mary")
                .email("m@d.com")
                .build()
        );

        List<User> all = userRepository.findAll();
        assertEquals(2, all.size());

        Optional<User> byId = userRepository.findById(userOne.getId());
        assertTrue(byId.isPresent());
        assertEquals("john", byId.get().getName());
    }

    @Test
    void deleteUserById_removesUser() {
        User userOne = userRepository.save(User.builder()
                .name("toDel")
                .email("d@d.com")
                .build()
        );
        Long id = userOne.getId();

        userRepository.deleteById(id);

        assertTrue(userRepository.findById(id).isEmpty());
        assertEquals(0, userRepository.findAll().size());
    }

    @Test
    void existsByEmail_shouldReturnTrueForExistingEmail() {
        User user = userRepository.save(User.builder()
                .name("test")
                .email("test@mail.com")
                .build()
        );

        boolean exists = userRepository.existsByEmail("test@mail.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmail_shouldReturnFalseForNonExistingEmail() {
        boolean exists = userRepository.existsByEmail("nonexistent@mail.com");

        assertFalse(exists);
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnTrueWhenEmailExistsForOtherUser() {
        User user1 = userRepository.save(User.builder()
                .name("user1")
                .email("user1@mail.com")
                .build()
        );
        User user2 = userRepository.save(User.builder()
                .name("user2")
                .email("user2@mail.com")
                .build()
        );

        boolean exists = userRepository.existsByEmailAndIdNot("user2@mail.com", user1.getId());

        assertTrue(exists);
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnFalseWhenEmailBelongsToSameUser() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.com")
                .build()
        );

        boolean exists = userRepository.existsByEmailAndIdNot("user@mail.com", user.getId());

        assertFalse(exists);
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnFalseForNonExistingEmail() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.com")
                .build()
        );

        boolean exists = userRepository.existsByEmailAndIdNot("nonexistent@mail.com", user.getId());

        assertFalse(exists);
    }

    @Test
    void findById_shouldReturnEmptyForNonExistingId() {
        Optional<User> result = userRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoUsers() {
        List<User> all = userRepository.findAll();

        assertTrue(all.isEmpty());
        assertEquals(0, all.size());
    }
}