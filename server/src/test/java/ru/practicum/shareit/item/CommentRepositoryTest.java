package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User author;
    private User owner;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;

    @BeforeEach
    void setUp() {
        author = userRepository.save(User.builder()
                .name("author")
                .email("author@mail.com")
                .build());

        owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner@mail.com")
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

        comment1 = commentRepository.save(Comment.builder()
                .text("Great drill!")
                .author(author)
                .item(item1)
                .created(LocalDateTime.now().minusDays(2))
                .build());

        comment2 = commentRepository.save(Comment.builder()
                .text("Very powerful")
                .author(author)
                .item(item1)
                .created(LocalDateTime.now().minusDays(1))
                .build());

        comment3 = commentRepository.save(Comment.builder()
                .text("Good hammer")
                .author(author)
                .item(item2)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void findByItemId_returnsCommentsForItem() {
        List<Comment> result = commentRepository.findByItemId(item1.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(comment -> comment.getItem().getId().equals(item1.getId())));
    }

    @Test
    void findByItemId_whenNoComments_returnsEmpty() {
        Item newItem = itemRepository.save(Item.builder()
                .name("New Item")
                .description("New item")
                .available(true)
                .owner(owner)
                .build());

        List<Comment> result = commentRepository.findByItemId(newItem.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemIdIn_returnsCommentsForMultipleItems() {
        List<Long> itemIds = List.of(item1.getId(), item2.getId());

        List<Comment> result = commentRepository.findByItemIdIn(itemIds);

        assertEquals(3, result.size());
    }

    @Test
    void findByItemIdIn_whenNoItems_returnsEmpty() {
        List<Comment> result = commentRepository.findByItemIdIn(Collections.emptyList());

        assertTrue(result.isEmpty());
    }

    @Test
    void save_findAll_findById_basicOperations() {
        List<Comment> all = commentRepository.findAll();
        Optional<Comment> byId = commentRepository.findById(comment1.getId());

        assertEquals(3, all.size());
        assertTrue(byId.isPresent());
        assertEquals("Great drill!", byId.get().getText());
    }

    @Test
    void delete_removesComment() {
        commentRepository.delete(comment1);

        Optional<Comment> result = commentRepository.findById(comment1.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void count_returnsTotalComments() {
        long count = commentRepository.count();

        assertEquals(3, count);
    }
}
