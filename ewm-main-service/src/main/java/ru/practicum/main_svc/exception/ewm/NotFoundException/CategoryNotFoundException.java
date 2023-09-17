package ru.practicum.main_svc.exception.ewm.NotFoundException;

import ru.practicum.main_svc.exception.NotFoundException;

import java.text.MessageFormat;

public class CategoryNotFoundException extends NotFoundException {
    public CategoryNotFoundException(Long catId) {
        super(MessageFormat.format("Category with id = {0} not found.", catId));
    }
}
