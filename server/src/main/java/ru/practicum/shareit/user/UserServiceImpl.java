package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAllUsers() {
        List<UserDto> allUsers = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            UserDto userDto = UserMapper.toDto(user);
            allUsers.add(userDto);
        }
        log.info("список пользователей {}", allUsers);
        return allUsers;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User saveUser = userRepository.save(user);
        log.info("создан {}", saveUser);
        return UserMapper.toDto(saveUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        if (userDto.getEmail() != null) {
            User userCheck = userRepository.findByEmail(userDto.getEmail());
            if (userCheck == null || userCheck.getId().equals(id)) {
                User user = UserMapper.toUser(userDto);
                user.setId(id);
                Optional<User> userOptional = userRepository.findById(id);
                if (userOptional.isEmpty()) {
                    throw new NotFoundException("Пользователь не найден");
                }
                if (user.getName() == null) user.setName(userOptional.get().getName());
                User updateUser = userRepository.save(user);
                log.info("обновлен {}", updateUser);
                return UserMapper.toDto(updateUser);
            } else {
                throw new InvalidEmailException("E-mail уже существует");
            }
        } else {
            User user = UserMapper.toUser(userDto);
            user.setId(id);
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }
            if (user.getName() == null) user.setName(userOptional.get().getName());
            user.setEmail(userOptional.get().getEmail());
            User updateUser = userRepository.save(user);
            log.info("обновлен {}", updateUser);
            return UserMapper.toDto(updateUser);
        }
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserMapper.toDto(user.get());
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
