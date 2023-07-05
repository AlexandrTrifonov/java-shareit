package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "Alexander",
                "test@mail.ru");
    }

    @SneakyThrows
    @Test
    void findAllUsers() {
        when(userService.findAllUsers()).thenReturn(List.of(userDto));

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(userDto)), result);
        verify(userService).findAllUsers();
    }

    @SneakyThrows
    @Test
    void createUser_whenUserIsValid_thenReturnedSavedUser() {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService).createUser(userDto);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserNameIsValid_thenReturnedUpdatedUser() {
        userDto.setName("updateAlexander");
        when(userService.updateUser(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userService).updateUser(1L, userDto);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }

    @SneakyThrows
    @Test
    void getUserById() {
        when(userService.getUserById(anyLong())).thenReturn(userDto);

        String result = mockMvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService).getUserById(1L);
    }
}