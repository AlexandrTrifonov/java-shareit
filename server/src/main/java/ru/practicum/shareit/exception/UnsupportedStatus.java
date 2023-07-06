package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedStatus extends RuntimeException {

    public UnsupportedStatus(String message) {
        super(message);
    }
}
