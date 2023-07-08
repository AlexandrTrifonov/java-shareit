package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.utils.Variables;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestHeader(value = Variables.USER_ID) Long bookerId,
                                    @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveBooking(@RequestHeader(value = Variables.USER_ID) Long userId,
                                     @PathVariable(value = "bookingId") Long id,
                                     @RequestParam("approved") Boolean approved) {
        return bookingService.approveBooking(userId, id, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBookingById(@RequestHeader(value = Variables.USER_ID) Long userId,
                                     @PathVariable(value = "bookingId") Long id) {
        return bookingService.getBookingById(userId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findAllBookingsUser(@RequestHeader(value = Variables.USER_ID) Long userId,
                                                @RequestParam(required = false, defaultValue = "ALL") String state,
                                                @RequestParam(value = "from", defaultValue = "0") int from,
                                                @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookingService.findAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findAllBookingsOwner(@RequestHeader(value = Variables.USER_ID) Long userId,
                                                 @RequestParam(required = false, defaultValue = "ALL") String state,
                                                 @RequestParam(value = "from", defaultValue = "0") int from,
                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookingService.findAllBookingsOwner(userId, state, from, size);
    }
}
