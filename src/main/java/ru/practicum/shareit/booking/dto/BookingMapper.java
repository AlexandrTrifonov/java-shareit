package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@UtilityClass
public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItemId(booking.getItem().getId()); //todo
        bookingDto.setItem(ItemMapper.toDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toDto(booking.getBooker()));
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }
}
