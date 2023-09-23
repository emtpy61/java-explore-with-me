package ru.practicum.main_svc.exception.ewm.NotFoundException;

import ru.practicum.main_svc.exception.NotFoundException;

import java.text.MessageFormat;

public class CommentNotFoundException extends NotFoundException {
    public CommentNotFoundException(Long catId) {
        super(MessageFormat.format("Comment with id = {0} not found.", catId));
    }
}
