package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIT {
    private final UserService userService;
    UserDto userDto;
    UserDto user2Dto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "Alexander",
                "test@mail.ru");

        user2Dto = new UserDto(
                2L,
                "Alexander2",
                "test2@mail.ru");
    }

    @Test
    void findAllUsers() {
        UserDto newUser = userService.createUser(userDto);
        UserDto newUser2 = userService.createUser(user2Dto);

        List<UserDto> list = (List<UserDto>) userService.findAllUsers();

        assertTrue(list.contains(newUser));
        assertTrue(list.contains(newUser2));
    }

    @Test
    void createUser() {
        UserDto newUser = userService.createUser(userDto);

        UserDto userById = userService.getUserById(newUser.getId());

        assertNotNull(userById);
        assertEquals(userById.getName(), newUser.getName());
    }

    @Test
    void updateUser() {
        userService.createUser(userDto);
        userDto.setName("NewAlexander");
        userDto.setEmail("newtest@mail.ru");

        UserDto updatedUser = userService.updateUser(userDto.getId(), userDto);

        assertThat(updatedUser.getName(), is("NewAlexander"));
        assertThat(updatedUser.getEmail(), is("newtest@mail.ru"));
    }

    @Test
    void updateUser_whenUserEmailIsNull() {
        userService.createUser(userDto);
        userDto.setName("NewAlexander");
        userDto.setEmail(null);

        UserDto updatedUser = userService.updateUser(userDto.getId(), userDto);

        assertThat(updatedUser.getName(), is("NewAlexander"));
        assertThat(updatedUser.getEmail(), is("test@mail.ru"));
    }

    @Test
    void deleteUser() {
        UserDto newUser = userService.createUser(userDto);
        userService.deleteUser(newUser.getId());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(newUser.getId()));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getUserById() {
        UserDto newUser = userService.createUser(user2Dto);

        UserDto userById = userService.getUserById(newUser.getId());

        assertThat(userById.getName(), is("Alexander2"));
        assertThat(userById.getEmail(), is("test2@mail.ru"));
    }
}