package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User owner;
    private User otherOwner;
    private User requestor;
    private ItemRequest request;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build());

        otherOwner = userRepository.save(User.builder()
                .name("otherOwner")
                .email("other@mail.com")
                .build());

        requestor = userRepository.save(User.builder()
                .name("requestor")
                .email("requestor@mail.com")
                .build());

        request = itemRequestRepository.save(ItemRequest.builder()
                .description("need tools")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());

        item1 = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        item2 = itemRepository.save(Item.builder()
                .name("Hammer")
                .description("Steel hammer")
                .available(true)
                .owner(owner)
                .build());

        item3 = itemRepository.save(Item.builder()
                .name("Saw")
                .description("Wood saw")
                .available(true)
                .owner(otherOwner)
                .build());
    }

    @Test
    void findByOwnerId_returnsOwnerItems() {
        List<Item> result = itemRepository.findByOwnerId(owner.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> item.getOwner().getId().equals(owner.getId())));
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Drill")));
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Hammer")));
    }

    @Test
    void findByOwnerId_whenNoItems_returnsEmpty() {
        itemRepository.deleteAll();

        List<Item> result = itemRepository.findByOwnerId(owner.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByRequestId_returnsItemsForRequest() {
        item1.setRequest(request);
        item2.setRequest(request);
        itemRepository.saveAll(List.of(item1, item2));

        List<Item> result = itemRepository.findByRequestId(request.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> item.getRequest().getId().equals(request.getId())));
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Drill")));
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Hammer")));
    }

    @Test
    void findByRequestId_whenNoItems_returnsEmpty() {
        List<Item> result = itemRepository.findByRequestId(request.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void searchAvailableItems_returnsMatchingAvailableItems() {
        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
        assertTrue(result.get(0).getAvailable());
    }

    @Test
    void searchAvailableItems_caseInsensitiveSearch() {
        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
    }

    @Test
    void searchAvailableItems_searchInNameAndDescription() {
        Item itemWithDrillInDescription = itemRepository.save(Item.builder()
                .name("Tool")
                .description("Powerful electric drill")
                .available(true)
                .owner(owner)
                .build());

        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Drill")));
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Tool")));
    }

    @Test
    void searchAvailableItems_whenNoMatches_returnsEmpty() {
        List<Item> result = itemRepository.searchAvailableItems("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchAvailableItems_onlyAvailableItems() {
        Item unavailableItem = itemRepository.save(Item.builder()
                .name("Unavailable Drill")
                .description("Broken drill")
                .available(false)
                .owner(owner)
                .build());

        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
        assertTrue(result.get(0).getAvailable());
    }

    @Test
    void save_findAll_findById_basicOperations() {
        List<Item> all = itemRepository.findAll();
        Optional<Item> byId = itemRepository.findById(item1.getId());

        assertEquals(3, all.size());
        assertTrue(byId.isPresent());
        assertEquals("Drill", byId.get().getName());
        assertEquals("Powerful drill", byId.get().getDescription());
        assertEquals(owner.getId(), byId.get().getOwner().getId());
        assertTrue(byId.get().getAvailable());
    }

    @Test
    void findByOwnerId_withMultipleOwners() {
        List<Item> owner1Items = itemRepository.findByOwnerId(owner.getId());
        List<Item> owner2Items = itemRepository.findByOwnerId(otherOwner.getId());

        assertEquals(2, owner1Items.size());
        assertEquals("Drill", owner1Items.get(0).getName());

        assertEquals(1, owner2Items.size());
        assertEquals("Saw", owner2Items.get(0).getName());
    }
}
