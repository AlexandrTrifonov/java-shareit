package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    UserDto userDto;
    User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "Alexander",
                "test@mail.ru");

        user = new User(
                1L,
                "Alexander",
                "test@mail.ru");
    }

    @Test
    void createUser_whenUserDtoIsValid_thenReturnedUserDto() {

        when(userRepository.save(any())).thenReturn(user);

        UserDto actualUserDto = userService.createUser(userDto);

        Assertions.assertEquals(userDto.getName(), actualUserDto.getName());
        Assertions.assertEquals(userDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void updateUser_whenUserDtoIsValid_thenReturnUpdatedUserDto() {
        when(userRepository.findByEmail(any())).thenReturn(null);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto actualUserDto = userService.updateUser(userDto.getId(), userDto);

        Assertions.assertEquals(userDto.getName(), actualUserDto.getName());
    }

    @Test
    void updateUser_whenUserDtoEmailIsNull_thenReturnUpdatedUserDto() {
        userDto.setEmail(null);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto actualUserDto = userService.updateUser(userDto.getId(), userDto);

        Assertions.assertEquals(userDto.getName(), actualUserDto.getName());
    }

    @Test
    void updateUser_whenUserDtoEmailIsNotValid_thenReturnNotFoundException() {
        when(userRepository.findByEmail(any())).thenReturn(null);
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUser(userDto.getId(), userDto));
    }

    @Test
    void getUserById_whenIdIsValid_thenReturnUserDto() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto actualUser = userService.getUserById(userDto.getId());

        Assertions.assertEquals(userDto.getName(), actualUser.getName());
    }

    @Test
    void findAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> actualListUser = (List<UserDto>) userService.findAllUsers();

        Assertions.assertEquals(userDto.getName(), actualListUser.get(0).getName());
    }
}