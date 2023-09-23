package ru.practicum.stat_svc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.stat_svc.exception.WrongDateTimeException;
import ru.practicum.stat_svc.model.ErrorMessage;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({WrongDateTimeException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleWrongDateTimeException(Exception e) {
        log.error(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleOtherExceptions(Exception e) {
        log.error(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }
}