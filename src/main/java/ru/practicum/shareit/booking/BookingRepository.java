package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerOrderByStartDesc(User booker, Pageable page);
    //   Page<Booking> findByBookerOrderByStartDesc(User booker, Pageable page);

//    List<Booking> findByBookerOrderByStartDesc(User booker, Pageable page);

//    List<Booking> findByBookerOrderByStart(User booker, Pageable page);

    @Query(nativeQuery = true, value = "select * from bookings where item_id = ?1 order by start_date desc")
    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    @Query(nativeQuery = true, value = "select * from bookings where item_id = ?1 and start_date > ?2 " +
            "and status like ?3 " +
            "order by start_date asc limit 1")
    Optional<Booking> getNextBooking(Long itemId, LocalDateTime now, String status);

    @Query(nativeQuery = true, value = "select * from bookings where item_id = ?1 and start_date < ?2 " +
            "and status like ?3 " +
            "order by start_date desc limit 1")
    Optional<Booking> getLastBooking(Long itemId, LocalDateTime now, String status);

    @Query(nativeQuery = true, value = "select status from bookings where booker_id = ?1 and " +
            "item_id = ?2 and end_date < ?3 limit 1 ")
    String checkStatusBooking(Long bookerId, Long itemId, LocalDateTime localDateTime);
}
