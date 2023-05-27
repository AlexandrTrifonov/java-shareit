package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private HashMap<Long, User> users = new HashMap<>();
    protected Set<String> emails = new HashSet<>();
    private  Long idUser = 1L;

    @Override
    public Collection<User> findAllUsers() {
    //    List<User> allUsers = users.values();
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
        String oldEmail = getUserById(user.getId()).getEmail();
        emails.remove(oldEmail);
        if (emails.contains(user.getEmail())) {
            log.warn("Ошибка при обновлении");
            NotFoundException.throwException("Ошибка при обновлении", user.getId());
        }
        User newUser = new User(user.getId(), user.getName(), user.getEmail());
        users.put(newUser.getId(), newUser);
        emails.add(user.getEmail());
        return newUser;
    }

    @Override
    public void deleteUser(Long id){
        User deleteUser = getUserById(id);
        emails.remove(deleteUser.getEmail());
        users.remove(id);
        log.info("Пользователь с id={} удален", id);
    }

    @Override
    public User getUserById(Long id) {
        User user = users.get(id);
        return user;
    }

    private Long generateUserId() {
        return idUser++;
    }
}
