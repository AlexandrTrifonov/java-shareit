package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserRepository {

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    Collection<User> findAllUsers();

    User getUserById(Long id);
}
