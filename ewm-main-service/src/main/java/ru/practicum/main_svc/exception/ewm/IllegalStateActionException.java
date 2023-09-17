package ru.practicum.main_svc.exception.ewm;

public class IllegalStateActionException extends RuntimeException {
    public IllegalStateActionException() {
        super("Invalid state action.");
    }
}