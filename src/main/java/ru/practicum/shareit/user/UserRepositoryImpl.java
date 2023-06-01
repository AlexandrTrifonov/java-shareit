package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    protected Set<String> emails = new HashSet<>();
    AtomicLong idUser = new AtomicLong(0);

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        User newUser = new User(generateUserId(), user.getName(), user.getEmail());
        users.put(newUser.getId(), newUser);
        emails.add(newUser.getEmail());
        return newUser;
    }

    @Override
    public User updateUser(User user) {
        User newUser = new User(user.getId(), user.getName(), user.getEmail());
        users.put(newUser.getId(), newUser);
        emails.add(user.getEmail());
        return newUser;
    }

    @Override
    public void deleteUser(Long id) {
        emails.remove(getUserById(id).getEmail());
        users.remove(id);
        log.info("Пользователь с id={} удален", id);
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    private Long generateUserId() {
        return idUser.incrementAndGet();
    }
}
