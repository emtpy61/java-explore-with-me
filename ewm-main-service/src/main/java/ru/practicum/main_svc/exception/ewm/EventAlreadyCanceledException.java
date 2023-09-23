package ru.practicum.main_svc.exception.ewm;

public class EventAlreadyCanceledException extends RuntimeException {
    public EventAlreadyCanceledException() {
        super("Event already canceled");
    }
}
