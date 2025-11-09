package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findByBookerIdOrderByStartDesc(@Param("userId") Long userId);

    // Текущие бронирования (идут прямо сейчас)
    Collection<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualAndStatusInOrderByStartDesc(
            @Param("userId") Long userId,
            @Param("time1") LocalDateTime time1,
            @Param("time2") LocalDateTime time2,
            @Param("statuses") Collection<BookingStatus> statuses);

    // Будущие бронирования (еще не начались)
    Collection<Booking> findByBookerIdAndStartGreaterThanAndStatusInOrderByStartDesc(
            @Param("userId") Long userId,
            @Param("time") LocalDateTime time,
            @Param("statuses") Collection<BookingStatus> statuses);

    // Завершенные бронирования (уже закончились)
    Collection<Booking> findByBookerIdAndEndLessThanAndStatusOrderByStartDesc(@Param("userId") Long userId,
                                                                              @Param("time") LocalDateTime time,
                                                                              @Param("status") BookingStatus status);

    Collection<Booking> findByBookerIdAndStatusOrderByStartDesc(@Param("userId") Long userId,
                                                                @Param("status") BookingStatus status);

    Collection<Booking> findByBookerIdAndStatusInOrderByStartDesc(@Param("userId") Long userId,
                                                                  @Param("statuses") Collection<BookingStatus> statuses);

    Collection<Booking> findByItemOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId);

    Collection<Booking> findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualAndStatusInOrderByStartDesc(
            @Param("ownerId") Long ownerId,
            @Param("time1") LocalDateTime time1,
            @Param("time2") LocalDateTime time2,
            @Param("statuses") Collection<BookingStatus> statuses);

    Collection<Booking> findByItemOwnerIdAndStartGreaterThanAndStatusInOrderByStartDesc(
            @Param("ownerId") Long ownerId,
            @Param("time") LocalDateTime time,
            @Param("statuses") Collection<BookingStatus> statuses);

    Collection<Booking> findByItemOwnerIdAndEndLessThanAndStatusOrderByStartDesc(
            @Param("ownerId") Long ownerId,
            @Param("time") LocalDateTime time,
            @Param("status") BookingStatus status);

    Collection<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(@Param("ownerId") Long ownerId,
                                                                   @Param("status") BookingStatus status);

    Collection<Booking> findByItemOwnerIdAndStatusInOrderByStartDesc(@Param("ownerId") Long ownerId,
                                                                     @Param("statuses") Collection<BookingStatus> statuses);

    // Последнее завершенное бронирование для вещи
    Optional<Booking> findTopByItemIdAndStatusAndEndLessThanOrderByEndDesc(
            @Param("itemId") Long itemId,
            @Param("status") BookingStatus status,
            @Param("time") LocalDateTime time);

    // Ближайшее следующее бронирование для вещи
    Optional<Booking> findTopByItemIdAndStatusAndStartGreaterThanOrderByStartAsc(
            @Param("itemId") Long itemId,
            @Param("status") BookingStatus status,
            @Param("time") LocalDateTime time);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item.id = :itemId AND b.status = :status " +
            "AND ((b.start BETWEEN :start AND :end) OR (b.end BETWEEN :start AND :end) " +
            "OR (b.start <= :start AND b.end >= :end))")
    boolean existsOverlappingBookings(@Param("itemId") Long itemId, @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end, @Param("status") BookingStatus status);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId AND b.end <= CURRENT_TIMESTAMP " +
            "AND b.status = :status")
    boolean existsByBookerIdAndItemIdAndEndBefore(@Param("userId") Long userId,
                                                  @Param("itemId") Long itemId,
                                                  @Param("status") BookingStatus status);
}