package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    Collection<Booking> findByBookerId(@Param("userId") Long userId);

    // Текущие бронирования (идут прямо сейчас)
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP " +
            "AND b.status IN (:status1, :status2) ORDER BY b.start DESC")
    Collection<Booking> findCurrentBookingsByBookerId(@Param("userId") Long userId,
                                                      @Param("status1") BookingStatus status1,
                                                      @Param("status2") BookingStatus status2);

    // Будущие бронирования (еще не начались)
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.start > CURRENT_TIMESTAMP AND b.status IN (:status1, :status2) ORDER BY b.start DESC")
    Collection<Booking> findFutureBookingsByBookerId(@Param("userId") Long userId,
                                                     @Param("status1") BookingStatus status1,
                                                     @Param("status2") BookingStatus status2);

    // Завершенные бронирования (уже закончились)
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP AND b.status = :status" +
            " ORDER BY b.start DESC")
    Collection<Booking> findPastBookingsByBookerId(@Param("userId") Long userId,
                                                   @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = :status ORDER BY b.start DESC")
    Collection<Booking> findWaitingBookingsByBookerId(@Param("userId") Long userId,
                                                      @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status IN (:status1, :status2) " +
            "ORDER BY b.start DESC")
    Collection<Booking> findRejectedBookingsByBookerId(@Param("userId") Long userId,
                                                       @Param("status1") BookingStatus status1,
                                                       @Param("status2") BookingStatus status2);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    Collection<Booking> findAllBookingsForOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start <= CURRENT_TIMESTAMP AND " +
            "b.end >= CURRENT_TIMESTAMP AND b.status IN (:status1, :status2) ORDER BY b.start DESC")
    Collection<Booking> findCurrentBookingsForOwner(@Param("ownerId") Long ownerId,
                                                    @Param("status1") BookingStatus status1,
                                                    @Param("status2") BookingStatus status2);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > CURRENT_TIMESTAMP AND " +
            "b.status IN (:status1, :status2) ORDER BY b.start DESC")
    Collection<Booking> findFutureBookingsForOwner(@Param("ownerId") Long ownerId,
                                                   @Param("status1") BookingStatus status1,
                                                   @Param("status2") BookingStatus status2);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < CURRENT_TIMESTAMP " +
            "AND b.status = :status ORDER BY b.start DESC")
    Collection<Booking> findPastBookingsForOwner(@Param("ownerId") Long ownerId,
                                                 @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    Collection<Booking> findWaitingBookingsForOwner(@Param("ownerId") Long ownerId,
                                                    @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status IN (:status1, :status2) " +
            "ORDER BY b.start DESC")
    Collection<Booking> findRejectedBookingsForOwner(@Param("ownerId") Long ownerId,
                                                     @Param("status1") BookingStatus status1,
                                                     @Param("status2") BookingStatus status2);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item.id = :itemId AND b.status = :status " +
            "AND ((b.start BETWEEN :start AND :end) OR (b.end BETWEEN :start AND :end) " +
            "OR (b.start <= :start AND b.end >= :end))")
    boolean existsOverlappingBookings(@Param("itemId") Long itemId, @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end, @Param("status") BookingStatus status);


    // Последнее завершенное бронирование для вещи
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.status = :status AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.end DESC")
    Optional<Booking> findLastBookingForItem(@Param("itemId") Long itemId,
                                             @Param("status") BookingStatus status);

    // Ближайшее следующее бронирование для вещи
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.status IN (:status1, :status2) AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC")
    Optional<Booking> findNextBookingForItem(@Param("itemId") Long itemId,
                                             @Param("status1") BookingStatus status1,
                                             @Param("status2") BookingStatus status2);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId AND b.end <= CURRENT_TIMESTAMP " +
            "AND b.status = :status")
    boolean existsByBookerIdAndItemIdAndEndBefore(@Param("userId") Long userId,
                                                  @Param("itemId") Long itemId,
                                                  @Param("status") BookingStatus status);
}