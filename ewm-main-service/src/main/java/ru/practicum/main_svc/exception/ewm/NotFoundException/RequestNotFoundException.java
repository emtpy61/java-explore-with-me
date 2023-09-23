package ru.practicum.main_svc.exception.ewm.NotFoundException;

import ru.practicum.main_svc.exception.NotFoundException;

import java.text.MessageFormat;

public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException(Long requestId) {
        super(MessageFormat.format("Request with id = {0} not found.", requestId));
    }
}