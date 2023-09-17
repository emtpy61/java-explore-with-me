package ru.practicum.main_svc.exception.ewm;

public class EventParticipantLimitException extends RuntimeException {
    public EventParticipantLimitException(String message) {
        super(message);
    }
}