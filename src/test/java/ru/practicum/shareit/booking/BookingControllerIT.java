package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    BookingService bookingService;
    @MockBean
    BookingRepository bookingRepository;
    UserDto userDto;
    ItemDto itemDto;
    BookingDto bookingDto;
    CommentDto commentDto;
    BookingDto nextBookingDto;
    BookingDto lastBookingDto;
    final LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    final LocalDateTime start = currentTime.plusHours(1);
    final LocalDateTime end = currentTime.plusHours(2);
    final LocalDateTime startLast = currentTime.minusHours(2);
    final LocalDateTime endLast = currentTime.minusHours(1);
    final LocalDateTime startNext = currentTime.plusHours(3);
    final LocalDateTime endNext = currentTime.plusDays(1);

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "Alexander",
                "test@mail.ru");
        commentDto = new CommentDto(
                1L,
                "text",
                "authorName",
                currentTime);
        nextBookingDto = new BookingDto(
                1L,
                startNext,
                endNext,
                1L,
                itemDto,
                userDto,
                1L,
                "APPROVED");
        lastBookingDto = new BookingDto(
                1L,
                startLast,
                endLast,
                1L,
                itemDto,
                userDto,
                1L,
                "APPROVED");
        itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true,
                1L,
                null,
                null,
                List.of(commentDto));
        bookingDto = new BookingDto(
                1L,
                start,
                end,
                1L,
                itemDto,
                userDto,
                1L,
                "WAITING");
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingIsValid_thenReturnedSavedBooking() {
        when(bookingService.createBooking(anyLong(), any())).thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
        verify(bookingService).createBooking(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookindIsNotValid_thenReturnedBadRequest() {
        bookingDto.setEnd(bookingDto.getStart().minusHours(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void approveBooking() {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName()), String.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName()), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));

        verify(bookingService).getBookingById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllUserBookingsTest() {
        when(bookingService.findAllBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName()), String.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName()), String.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @SneakyThrows
    @Test
    void findAllBookingsOwner() {
        when(bookingService.findAllBookingsOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName()), String.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName()), String.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }
}