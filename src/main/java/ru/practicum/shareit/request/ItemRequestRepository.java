package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findByRequestorIdNot(Long ownerId);

    @Query("SELECT DISTINCT ir FROM ItemRequest ir " +
            "LEFT JOIN FETCH ir.items " + // items - это вещи, созданные с request_id = ir.id
            "WHERE ir.requestor.id = :userId " +
            "ORDER BY ir.timeCreated DESC")
    List<ItemRequest> findUserRequestsWithItems(@Param("userId") Long userId);

    @Query("SELECT ir FROM ItemRequest ir " +
            "LEFT JOIN FETCH ir.items " +
            "WHERE ir.id = :requestId")
    Optional<ItemRequest> findByIdWithItems(@Param("requestId") Long requestId);

    ItemRequest create(ItemRequest request);

    void delete(Long id);
}
