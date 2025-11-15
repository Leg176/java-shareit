package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
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

    private User user1;
    private User user2;
    private User owner;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest requestWithItems;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder()
                .name("user1")
                .email("user1@mail.com")
                .build());

        user2 = userRepository.save(User.builder()
                .name("user2")
                .email("user2@mail.com")
                .build());

        owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build());

        request1 = itemRequestRepository.save(ItemRequest.builder()
                .description("request from user2")
                .requestor(user2)
                .created(LocalDateTime.now().minusHours(1))
                .build());

        request2 = itemRequestRepository.save(ItemRequest.builder()
                .description("another request from user2")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build());

        requestWithItems = itemRequestRepository.save(ItemRequest.builder()
                .description("request with items")
                .requestor(user1)
                .created(LocalDateTime.now().minusDays(1))
                .build());
    }

    @Test
    void findByRequestorIdNot_returnsOtherUsersRequests() {
        Collection<ItemRequest> result = itemRequestRepository.findByRequestorIdNot(user1.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getRequestor().getId().equals(user2.getId())));
        assertTrue(result.stream().anyMatch(r -> r.getDescription().equals("request from user2")));
        assertTrue(result.stream().anyMatch(r -> r.getDescription().equals("another request from user2")));
    }

    @Test
    void findByRequestorIdNot_returnsOnlyOtherUsersRequests() {
        Collection<ItemRequest> result = itemRequestRepository.findByRequestorIdNot(user1.getId());

        // user1 должен видеть только запросы от user2
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getRequestor().getId().equals(user2.getId())));
    }

    @Test
    void findUserRequestsWithItems_returnsUserRequestsWithItems() {
        List<ItemRequest> result = itemRequestRepository.findUserRequestsWithItems(user2.getId());

        assertEquals(2, result.size());
        assertEquals(request2.getId(), result.get(0).getId());
        assertEquals(request1.getId(), result.get(1).getId());
        assertEquals(user2.getId(), result.get(0).getRequestor().getId());
        assertEquals(user2.getId(), result.get(1).getRequestor().getId());
    }

    @Test
    void findUserRequestsWithItems_whenNoRequests_returnsEmpty() {
        User userWithoutRequests = userRepository.save(User.builder()
                .name("noRequestsUser")
                .email("norequests@mail.com")
                .build());

        List<ItemRequest> result = itemRequestRepository.findUserRequestsWithItems(userWithoutRequests.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findUserRequestsWithItems_withItems_returnsRequestsWithItems() {
        entityManager.persist(Item.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .owner(owner)
                .request(requestWithItems)
                .build());
        entityManager.persist(Item.builder()
                .name("item2")
                .description("description2")
                .available(true)
                .owner(owner)
                .request(requestWithItems)
                .build());
        entityManager.flush();
        entityManager.clear();

        List<ItemRequest> result = itemRequestRepository.findUserRequestsWithItems(user1.getId());

        assertEquals(1, result.size());
        assertEquals(requestWithItems.getId(), result.get(0).getId());
        assertNotNull(result.get(0).getItems());
        assertEquals(2, result.get(0).getItems().size());
        assertTrue(result.get(0).getItems().stream().anyMatch(item -> item.getName().equals("item1")));
        assertTrue(result.get(0).getItems().stream().anyMatch(item -> item.getName().equals("item2")));
    }

    @Test
    void findByIdWithItems_returnsRequestWithItems() {
        entityManager.persist(Item.builder()
                .name("test item")
                .description("item description")
                .available(true)
                .owner(owner)
                .request(requestWithItems)
                .build());
        entityManager.flush();
        entityManager.clear();

        Optional<ItemRequest> result = itemRequestRepository.findByIdWithItems(requestWithItems.getId());

        assertTrue(result.isPresent());
        assertEquals(requestWithItems.getId(), result.get().getId());
        assertEquals("request with items", result.get().getDescription());
        assertNotNull(result.get().getItems());
        assertEquals(1, result.get().getItems().size());
        assertEquals("test item", result.get().getItems().get(0).getName());
    }

    @Test
    void findByIdWithItems_whenNoItems_returnsRequestWithoutItems() {
        Optional<ItemRequest> result = itemRequestRepository.findByIdWithItems(request1.getId());

        assertTrue(result.isPresent());
        assertEquals(request1.getId(), result.get().getId());
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
        List<ItemRequest> all = itemRequestRepository.findAll();
        Optional<ItemRequest> byId = itemRequestRepository.findById(request1.getId());

        assertEquals(3, all.size());
        assertTrue(byId.isPresent());
        assertEquals("request from user2", byId.get().getDescription());
        assertEquals(user2.getId(), byId.get().getRequestor().getId());
    }

    @Test
    void findByRequestorIdNot_withMultipleUsers() {
        User user3 = userRepository.save(User.builder()
                .name("user3")
                .email("user3@mail.com")
                .build());

        ItemRequest user3Request = itemRequestRepository.save(ItemRequest.builder()
                .description("request from user3")
                .requestor(user3)
                .created(LocalDateTime.now())
                .build());

        Collection<ItemRequest> user1Result = itemRequestRepository.findByRequestorIdNot(user1.getId());
        Collection<ItemRequest> user2Result = itemRequestRepository.findByRequestorIdNot(user2.getId());
        Collection<ItemRequest> user3Result = itemRequestRepository.findByRequestorIdNot(user3.getId());

        assertEquals(3, user1Result.size());
        assertEquals(2, user2Result.size());
        assertEquals(3, user3Result.size());
        assertTrue(user1Result.stream().allMatch(r ->
                r.getRequestor().getId().equals(user2.getId()) ||
                        r.getRequestor().getId().equals(user3.getId())));
    }
}