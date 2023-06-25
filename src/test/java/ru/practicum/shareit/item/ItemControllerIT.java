package ru.practicum.shareit.item;

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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemService itemService;
    ItemDto itemDto;
    UserDto userDto;
    UserDto bookerDto;
    CommentDto commentDto;
    BookingDto nextBookingDto;
    BookingDto lastBookingDto;
    final LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    final LocalDateTime startLast = currentTime.minusHours(2);
    final LocalDateTime endLast = currentTime.minusHours(1);
    final LocalDateTime startNext = currentTime.plusHours(1);
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
                nextBookingDto,
                lastBookingDto,
                List.of(commentDto));
    }

    @SneakyThrows
    @Test
    void createItem_whenItemIsValid_thenReturnedSavedItem() {
        when(itemService.createItem(anyLong(), any())).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService).createItem(1L, itemDto);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        itemDto.setName("updateName");
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.itemId", is(itemDto.getNextBooking().getItemId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.item", is(itemDto.getNextBooking().getItem()), ItemDto.class))
                .andExpect(jsonPath("$.nextBooking.booker", is(itemDto.getNextBooking().getBooker()), UserDto.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemDto.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.status", is(itemDto.getNextBooking().getStatus()), String.class));

        verify(itemService).updateItem(1L, 1L, itemDto);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDto);

        String result = mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService).getItem(1L, 1L);
    }

    @SneakyThrows
    @Test
    void createComment() {
        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
        verify(itemService).createComment(anyLong(), anyLong(), any());
    }
}