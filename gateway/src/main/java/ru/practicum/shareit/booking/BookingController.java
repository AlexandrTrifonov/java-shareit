package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable(name = "bookingId") Long bookingId,
                                                 @RequestParam(name = "approved") Boolean approved) {
        System.out.println("1111111111111111");
        System.out.println(userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(required = false, defaultValue = "ALL") String state,
                                                      @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        return bookingClient.findAllBookingsUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(required = false, defaultValue = "ALL") String state,
                                                       @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        return bookingClient.findAllBookingsOwner(userId, state, from, size);
    }

}
