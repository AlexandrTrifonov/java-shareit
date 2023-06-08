package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

/*    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    Collection<User> findAllUsers();

    User getUserById(Long id);*/
}
