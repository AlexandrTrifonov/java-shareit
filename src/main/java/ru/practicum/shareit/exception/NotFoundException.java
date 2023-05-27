package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {

    public static void throwException(String format, Long id) {
        String message = format + " c id=" + id;
        throw new NotFoundException(message);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
