package ru.practicum.main_svc.exception.ewm.NotFoundException;

import ru.practicum.main_svc.exception.NotFoundException;

import java.text.MessageFormat;

public class CompilationNotFoundException extends NotFoundException {
    public CompilationNotFoundException(Long compId) {
        super(MessageFormat.format("Category with id = {0} not found.", compId));
    }
}