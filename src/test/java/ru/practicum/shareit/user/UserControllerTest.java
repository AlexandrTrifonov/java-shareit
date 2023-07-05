package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        userDto = new UserDto(
                1L,
                "Alexander",
                "test@mail.ru");
    }

    @Test
    void findAllUsers_thenReturnAllUsers() throws Exception {
        when(userService.findAllUsers())
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())));
    }

    @Test
    void createUser_whenUserIsValid_thenReturnedSavedUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUser_whenEmailIsValid_thenReturnSavedUserWithNewEmail() throws Exception {
        String newEmail = "newEmail";
        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userDto);

        userDto.setEmail(newEmail);
        mockMvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(newEmail)));
    }

    @Test
    void updateUser_whenEmailIsEmpty_thenInvalidEmailExceptionThrown() throws Exception {
        when(userService.updateUser(anyLong(), any()))
                .thenThrow(new InvalidEmailException("Пользователь не найден"));

        mockMvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_whenUserIsExist_thenGetUser() throws Exception {
        when(userService.getUserById(any()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUserById_whenUserIsNotExist_thenNotFoundExceptionThrown() throws Exception {
        when(userService.getUserById(any()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}