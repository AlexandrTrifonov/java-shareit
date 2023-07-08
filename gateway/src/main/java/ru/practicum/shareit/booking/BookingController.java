package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.utils.Variables;

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
    public ResponseEntity<Object> createBooking(@RequestHeader(value = Variables.USER_ID) Long bookerId,
                                                @Valid @RequestBody BookingDto bookingDto) {
        log.info("Запрос к клиенту на получение брони");
        return bookingClient.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(value = Variables.USER_ID) Long userId,
                                                 @PathVariable(name = "bookingId") Long bookingId,
                                                 @RequestParam(name = "approved") Boolean approved) {
        log.info("Запрос к клиенту на одобрение брони");
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(value = Variables.USER_ID) Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        log.info("Запрос к клиенту на получение брони");
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsUser(@RequestHeader(value = Variables.USER_ID) Long userId,
                                                      @RequestParam(required = false, defaultValue = "ALL") String state,
                                                      @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Запрос к клиенту на получение всей брони");
        return bookingClient.findAllBookingsUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsOwner(@RequestHeader(value = Variables.USER_ID) Long userId,
                                                       @RequestParam(required = false, defaultValue = "ALL") String state,
                                                       @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Запрос к клиенту на получение всей брони");
        return bookingClient.findAllBookingsOwner(userId, state, from, size);
    }

}
