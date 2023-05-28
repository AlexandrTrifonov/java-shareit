package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    Collection<UserDto> findAllUsers();

    UserDto getUserById(Long id);
}
