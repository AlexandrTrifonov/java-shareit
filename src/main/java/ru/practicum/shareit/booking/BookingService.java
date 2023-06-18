package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long bookerId, BookingDto bookingDto);

    BookingDto approveBooking(Long userId, Long id, Boolean approved);

    BookingDto getBookingById(Long userId, Long id);

    List<BookingDto> findAllBookings(Long userId, String state);

    List<BookingDto> findAllBookingsOwner(Long userId, String state);
}
