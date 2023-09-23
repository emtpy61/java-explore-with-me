package ru.practicum.main_svc.exception.ewm.NotFoundException;

import ru.practicum.main_svc.exception.NotFoundException;

import java.text.MessageFormat;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super(MessageFormat.format("User with id = {0} not found.", userId));
    }
}