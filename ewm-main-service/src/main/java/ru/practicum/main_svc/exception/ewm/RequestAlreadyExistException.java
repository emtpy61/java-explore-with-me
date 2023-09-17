package ru.practicum.main_svc.exception.ewm;

public class RequestAlreadyExistException extends RuntimeException {
    public RequestAlreadyExistException(String message) {
        super(message);
    }

    public RequestAlreadyExistException() {
        super("Request already created");
    }
}