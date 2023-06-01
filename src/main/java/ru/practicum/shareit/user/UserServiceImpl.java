package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl userRepository;

    @Override
    public Collection<UserDto> findAllUsers() {
        List<UserDto> allUsers = new ArrayList<>();
        for (User user : userRepository.findAllUsers()) {
            UserDto userDto = UserMapper.toDto(user);
            allUsers.add(userDto);
        }
        log.info("список пользователей {}", allUsers);
        return allUsers;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.emails.contains(userDto.getEmail())) {
            log.warn("Ошибка - Email уже существует");
            throw new InvalidEmailException("Email уже существует");
        }
        User user = UserMapper.toUser(userDto);
        User saveUser = userRepository.createUser(user);
        log.info("создан {}", saveUser);
        return UserMapper.toDto(saveUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        UserDto userDtoCheck = getUserById(id);
        String oldEmail = getUserById(id).getEmail();
        if (userDto.getEmail() != null) {
            userRepository.emails.remove(oldEmail);
        }
        if (userRepository.emails.contains(userDto.getEmail())) {
            userRepository.emails.add(oldEmail);
            log.warn("Ошибка - Email уже существует");
            throw new InvalidEmailException("Email уже существует");
        }
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        if (user.getName() == null) user.setName(userDtoCheck.getName());
        if (user.getEmail() == null) user.setEmail(userDtoCheck.getEmail());

        User updateUser = userRepository.updateUser(user);
        log.info("обновлен {}", UserMapper.toDto(updateUser));
        return UserMapper.toDto(updateUser);
    }

    @Override
    public void deleteUser(Long id) {
        UserDto userDtoCheck = getUserById(id);
        if (userDtoCheck == null) {
            log.warn("Ошибка при удалении");
            NotFoundException.throwException("Ошибка при удалении", id);
        }
        userRepository.deleteUser(id);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            log.warn("Ошибка при получении пользователя с id={}", id);
            NotFoundException.throwException("Ошибка при получении пользователя", id);
        }
        UserDto userDto = UserMapper.toDto(user);
        log.info("получен {}", userDto);
        return userDto;
    }
}
