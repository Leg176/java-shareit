package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();

    private User author;
    private Item item;
    private Comment comment;
    private NewCommentRequest newCommentRequest;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        User owner = User.builder()
                .id(2L)
                .name("Jane")
                .email("jane@example.com")
                .build();

        item = Item.builder()
                .id(10L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Great tool!")
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Excellent condition");
    }

    @Test
    void mapToComment_shouldMapAllFields() {
        Comment result = commentMapper.mapToComment(newCommentRequest, author, item);

        assertThat(result.getId()).isNull();
        assertThat(result.getText()).isEqualTo("Excellent condition");
        assertThat(result.getAuthor()).isEqualTo(author);
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void mapToComment_shouldTrimText() {
        NewCommentRequest requestWithSpaces = new NewCommentRequest();
        requestWithSpaces.setText("  Excellent condition  ");

        Comment result = commentMapper.mapToComment(requestWithSpaces, author, item);

        assertThat(result.getText()).isEqualTo("  Excellent condition  ");
    }

    @Test
    void mapToCommentDto_shouldMapAllFields() {
        CommentDto result = commentMapper.mapToCommentDto(comment);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Great tool!");
        assertThat(result.getAuthorName()).isEqualTo("John");
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void mapToCommentDto_shouldHandleAuthorWithNullName() {
        User authorWithoutName = User.builder()
                .id(1L)
                .name(null)
                .email("john@example.com")
                .build();

        Comment commentWithNullAuthorName = Comment.builder()
                .id(1L)
                .text("Great tool!")
                .author(authorWithoutName)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        CommentDto result = commentMapper.mapToCommentDto(commentWithNullAuthorName);

        assertThat(result.getAuthorName()).isNull();
    }

    @Test
    void mapToCommentDtoList_shouldMapListOfComments() {
        Comment comment2 = Comment.builder()
                .id(2L)
                .text("Very useful")
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        List<Comment> comments = List.of(comment, comment2);
        List<CommentDto> result = commentMapper.mapToCommentDtoList(comments);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getText()).isEqualTo("Great tool!");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getText()).isEqualTo("Very useful");
    }

    @Test
    void mapToCommentDtoList_shouldHandleEmptyList() {
        List<CommentDto> result = commentMapper.mapToCommentDtoList(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void mapToCommentDtoList_shouldHandleSingleComment() {
        List<CommentDto> result = commentMapper.mapToCommentDtoList(List.of(comment));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getText()).isEqualTo("Great tool!");
    }

    @Test
    void mapToComment_shouldHandleDifferentTextLengths() {
        NewCommentRequest shortTextRequest = new NewCommentRequest();
        shortTextRequest.setText("OK");

        Comment result = commentMapper.mapToComment(shortTextRequest, author, item);

        assertThat(result.getText()).isEqualTo("OK");
    }
}
