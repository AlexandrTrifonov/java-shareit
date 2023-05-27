package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;

import static ru.practicum.shareit.user.ValidateUser.validateUser;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    Collection<UserDto> findAllUsers();

    UserDto getUserById(Long id);
}
