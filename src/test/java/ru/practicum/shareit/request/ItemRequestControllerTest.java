package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;
    private ItemRequestDto itemRequestDto;
    String userHeader = "X-Sharer-User-Id";
    String requestId = "requestId";
    final LocalDateTime currentTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        itemRequestDto = new ItemRequestDto(
                1L,
                "Описание реквеста",
                null,
                null,
                null);
    }

    @Test
    void createItemRequest_whenItemRequestIsValid_thenReturnedSavedItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(userHeader, 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void findItemRequestsOwner() throws Exception {
        when(itemRequestService.findItemRequestsOwner(any()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header(userHeader, 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(any(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header(userHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void findAllItemRequests() throws Exception {
        when(itemRequestService.findAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header(userHeader, 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }
}