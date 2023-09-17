package ru.practicum.main_svc.exception.ewm;

public class WrongRequestStatusException extends RuntimeException {
    public WrongRequestStatusException(String message) {
        super(message);
    }
}