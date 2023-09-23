package ru.practicum.main_svc.service;

import ru.practicum.main_svc.dto.comment.CommentDto;
import ru.practicum.main_svc.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getCommentsByUser(Long userId, Integer from, Integer size);

    List<CommentDto> getCommentsByEvent(Long eventId, Integer from, Integer size);

    CommentDto updateComment(NewCommentDto newCommentDto, Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    void deleteComment(Long userId, Long commentId);
}
