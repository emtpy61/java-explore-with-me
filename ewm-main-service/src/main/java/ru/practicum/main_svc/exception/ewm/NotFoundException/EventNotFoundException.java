package ru.practicum.main_svc.exception.ewm.NotFoundException;

import ru.practicum.main_svc.exception.NotFoundException;

import java.text.MessageFormat;

public class EventNotFoundException extends NotFoundException {
    public EventNotFoundException(Long eventId) {
        super(MessageFormat.format("Event with id = {0} not found.", eventId));
    }
}