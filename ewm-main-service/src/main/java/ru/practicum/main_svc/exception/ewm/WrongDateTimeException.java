package ru.practicum.main_svc.exception.ewm;

public class WrongDateTimeException extends RuntimeException {
    public WrongDateTimeException(String message) {
        super(message);
    }
}