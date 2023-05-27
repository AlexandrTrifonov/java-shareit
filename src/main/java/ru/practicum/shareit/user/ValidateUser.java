package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidateUser {

    public static void validateUser(UserDto userDto) {
    /*    if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Поле e-mail не заполнено");
            throw new ValidationException("Поле e-mail не заполнено");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Поле e-mail не содержит @");
            throw new ValidationException("Поле e-mail не содержит @");
        }

        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.warn("Поле логин не заполнено или содержит пробелы");
            throw new ValidationException("Поле логин не заполнено или содержит пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения указана в будущем");
            throw new ValidationException("Дата рождения указана в будущем");
        }*/
    }
}
