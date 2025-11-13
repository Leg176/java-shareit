package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByRequestorIdNot_returnsOtherUsersRequests() {
        User user1 = userRepository.save(User.builder()
                .name("user1")
                .email("user1@mail.com")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("user2")
                .email("user2@mail.com")
                .build());

        ItemRequest request1 = itemRequestRepository.save(ItemRequest.builder()
                .description("request from user2")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build());
        ItemRequest request2 = itemRequestRepository.save(ItemRequest.builder()
                .description("another request from user2")
                .requestor(user2)
                .created(LocalDateTime.now().minusHours(1))
                .build());

        Collection<ItemRequest> result = itemRequestRepository.findByRequestorIdNot(user1.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getRequestor().getId().equals(user2.getId())));
    }

    @Test
    void findByRequestorIdNot_whenNoOtherUsers_returnsEmpty() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.com")
                .build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("user's request")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());

        Collection<ItemRequest> result = itemRequestRepository.findByRequestorIdNot(user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findUserRequestsWithItems_returnsUserRequestsWithItems() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.com")
                .build());

        ItemRequest request1 = itemRequestRepository.save(ItemRequest.builder()
                .description("first request")
                .requestor(user)
                .created(LocalDateTime.now().minusDays(1))
                .build());
        ItemRequest request2 = itemRequestRepository.save(ItemRequest.builder()
                .description("second request")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());

        List<ItemRequest> result = itemRequestRepository.findUserRequestsWithItems(user.getId());

        assertEquals(2, result.size());
        assertEquals(request2.getId(), result.get(0).getId());
        assertEquals(request1.getId(), result.get(1).getId());
        assertEquals(user.getId(), result.get(0).getRequestor().getId());
        assertEquals(user.getId(), result.get(1).getRequestor().getId());
    }

    @Test
    void findUserRequestsWithItems_whenNoRequests_returnsEmpty() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.com")
                .build());

        List<ItemRequest> result = itemRequestRepository.findUserRequestsWithItems(user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findUserRequestsWithItems_withItems_returnsRequestsWithItems() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.com")
                .build());
        User owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build());

        ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
                .description("request with items")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());

        entityManager.persist(Item.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .owner(owner)
                .request(request)
                .build());
        entityManager.persist(Item.builder()
                .name("item2")
                .description("description2")
                .available(true)
                .owner(owner)
                .request(request)
                .build());
        entityManager.flush();
        entityManager.clear();

        List<ItemRequest> result = itemRequestRepository.findUserRequestsWithItems(user.getId());

        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
        assertNotNull(result.get(0).getItems());
        assertEquals(2, result.get(0).getItems().size());
    }

    @Test
    void findByIdWithItems_returnsRequestWithItems() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.com")
                .build());
        User owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build());

        ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
                .description("test request")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());

        entityManager.persist(Item.builder()
                .name("test item")
                .description("item description")
                .available(true)
                .owner(owner)
                .request(request)
                .build());
        entityManager.flush();
        entityManager.clear();

        Optional<ItemRequest> result = itemRequestRepository.findByIdWithItems(request.getId());

        assertTrue(result.isPresent());
        assertEquals(request.getId(), result.get().getId());
        assertEquals("test request", result.get().getDescription());
        assertNotNull(result.get().getItems());
        assertEquals(1, result.get().getItems().size());
        assertEquals("test item", result.get().getItems().get(0).getName());
    }

    @Test
    void findByIdWithItems_whenNoItems_returnsRequestWithoutItems() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.com")
                .build());

        ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
                .description("request without items")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());

        Optional<ItemRequest> result = itemRequestRepository.findByIdWithItems(request.getId());

        assertTrue(result.isPresent());
        assertEquals(request.getId(), result.get().getId());
        assertNotNull(result.get().getItems());
        assertTrue(result.get().getItems().isEmpty());
    }

    @Test
    void findByIdWithItems_whenNotFound_returnsEmpty() {
        Optional<ItemRequest> result = itemRequestRepository.findByIdWithItems(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void save_findAll_findById_basicOperations() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.com")
                .build());

        ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
                .description("test description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());

        List<ItemRequest> all = itemRequestRepository.findAll();
        Optional<ItemRequest> byId = itemRequestRepository.findById(request.getId());

        assertEquals(1, all.size());
        assertTrue(byId.isPresent());
        assertEquals("test description", byId.get().getDescription());
        assertEquals(user.getId(), byId.get().getRequestor().getId());
    }
}