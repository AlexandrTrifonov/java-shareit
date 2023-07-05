package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemRequestService itemRequestService;
    ItemRequestDto itemRequesDto;
    UserDto requestor;
    final LocalDateTime createdDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    @BeforeEach
    void setUp() {
        requestor = new UserDto(1L, "Alexander", "test@mail.ru");
        itemRequesDto = new ItemRequestDto(1L, "description", requestor, createdDate, null);
    }

    @SneakyThrows
    @Test
    void createItemRequest() {
        when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(itemRequesDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequesDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequesDto), result);
        verify(itemRequestService).createItemRequest(1L, itemRequesDto);
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequesDto);

        String result = mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequesDto), result);
        verify(itemRequestService).getItemRequestById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getItemRequestById_whenItemRequestNotFound_thenReturnedNotFoundException() {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Не найден запрос"));

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}