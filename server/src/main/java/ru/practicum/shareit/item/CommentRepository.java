package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.entity.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(@Param("itemId") Long itemId);

    List<Comment> findByItemIdIn(@Param("itemIds") List<Long> itemIds);
}
