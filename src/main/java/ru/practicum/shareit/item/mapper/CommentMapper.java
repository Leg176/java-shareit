package ru.practicum.shareit.item.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CommentMapper {
    public Comment mapToComment(NewCommentRequest request, User author, Item item) {
        if (author == null) {
            throw new IllegalArgumentException("Author не может быть null");
        }
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null");
        }
        return Comment.builder()
                .text(request.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public CommentDto mapToCommentDto(Comment comment) {
        if (comment == null) {
            throw new BadRequestException("Comment не может быть равен null");
        }
        return  CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public List<CommentDto> mapToCommentDtoList(List<Comment> comments) {
        return comments.stream().map(this::mapToCommentDto).toList();
    }
}
