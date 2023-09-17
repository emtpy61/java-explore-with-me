package ru.practicum.main_svc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main_svc.dto.error.ApiError;
import ru.practicum.main_svc.exception.NotFoundException;
import ru.practicum.main_svc.exception.ewm.*;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final Exception exception) {
        return new ApiError(HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase(),
                "The required object was not found.",
                exception.getMessage(),
                LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler({ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class,
            WrongDateTimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final Exception exception) {
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase(),
                "Incorrectly made request.",
                exception.getMessage(),
                LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler({
            NameAlreadyExistException.class,
            CategoryNotEmptyException.class,
            EventAlreadyPublishedException.class,
            EventAlreadyCanceledException.class,
            EventAlreadyPublishedException.class,
            RequestAlreadyExistException.class,
            EventNotPublishedException.class,
            EventParticipantLimitException.class,
            ParticipantLimitException.class,
            WrongRequestStatusException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final Exception exception) {
        return new ApiError(HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(),
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbidenException(final Exception exception) {
        return new ApiError(HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase(),
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                LocalDateTime.now().format(dateFormatter));
    }
}