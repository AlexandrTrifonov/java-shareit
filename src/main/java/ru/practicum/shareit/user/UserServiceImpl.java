package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.user.ValidateUser.validateUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRepositoryImpl userRepositoryImpl;

    @Override
    public Collection<UserDto> findAllUsers() {
        List<UserDto> allUsers = new ArrayList<>();
        for (User user : userRepository.findAllUsers()) {
            UserDto userDto = UserMapper.userToDto(user);
            allUsers.add(userDto);
        }
        log.info("список пользователей {}", allUsers);
        return allUsers;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepositoryImpl.emails.contains(userDto.getEmail())) {
            log.warn("Ошибка - Email уже существует");
            throw new InvalidEmailException("Email уже существует");
        }
        User user = UserMapper.dtoToUser(userDto);
        User saveUser = userRepositoryImpl.createUser(user);
        log.info("создан {}", saveUser);
        return UserMapper.userToDto(saveUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        UserDto userDtoCheck = getUserById(id);
        if (userDtoCheck == null) {
            log.warn("Ошибка при обновлении");
            NotFoundException.throwException("Ошибка при обновлении", userDto.getId());
        }
        User user = UserMapper.dtoToUser(userDto);
        user.setId(id);
        if (user.getName() == null) user.setName(userDtoCheck.getName());
        if (user.getEmail() == null) user.setEmail(userDtoCheck.getEmail());
        User updateUser = userRepositoryImpl.updateUser(user);
        log.info("обновлен {}", UserMapper.userToDto(updateUser));
    //    validateUser(user);
        return UserMapper.userToDto(updateUser);
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
    public UserDto getUserById(Long id)  {
        User user = userRepositoryImpl.getUserById(id);
        if (user == null) {
            log.warn("Ошибка при получении пользователя с id={}", id);
            NotFoundException.throwException("Ошибка при получении пользователя", id);
        }
        UserDto userDto = UserMapper.userToDto(user);
        log.info("получен {}", userDto);
        return userDto;
    }
}
