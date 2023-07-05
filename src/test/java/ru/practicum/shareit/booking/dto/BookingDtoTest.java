package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDto> json;
    final LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    final LocalDateTime end = start.plusHours(2);
    final LocalDateTime startLast = start.minusHours(2);
    final LocalDateTime endLast = start.minusHours(1);
    final LocalDateTime startNext = start.plusHours(3);
    final LocalDateTime endNext = start.plusDays(1);

    @Test
    void testItemDto() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "Alexander",
                "test@mail.ru");
        CommentDto commentDto = new CommentDto(
                1L,
                "text",
                "authorName",
                start);
        ItemDto itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true,
                1L,
                null,
                null,
                List.of(commentDto));
        BookingDto nextBookingDto = new BookingDto(
                1L,
                startNext,
                endNext,
                1L,
                itemDto,
                userDto,
                1L,
                "APPROVED");
        BookingDto lastBookingDto = new BookingDto(
                1L,
                startLast,
                endLast,
                1L,
                itemDto,
                userDto,
                1L,
                "APPROVED");
        BookingDto bookingDto = new BookingDto(
                1L,
                start,
                end,
                1L,
                itemDto,
                userDto,
                1L,
                "WAITING");

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Alexander");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("test@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}