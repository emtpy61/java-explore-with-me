package ru.practicum.main_svc.exception.ewm;

public class EventAlreadyPublishedException extends RuntimeException {
    public EventAlreadyPublishedException() {
        super("Event already published");
    }
}
