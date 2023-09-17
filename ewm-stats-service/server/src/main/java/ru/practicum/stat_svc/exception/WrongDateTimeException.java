package ru.practicum.stat_svc.exception;

public class WrongDateTimeException extends RuntimeException {
    public WrongDateTimeException(String message) {
        super(message);
    }
}