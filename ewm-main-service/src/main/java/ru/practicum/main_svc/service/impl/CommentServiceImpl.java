package ru.practicum.main_svc.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_svc.dto.comment.CommentDto;
import ru.practicum.main_svc.dto.comment.NewCommentDto;
import ru.practicum.main_svc.enums.EventState;
import ru.practicum.main_svc.exception.ewm.AccessDeniedException;
import ru.practicum.main_svc.exception.ewm.EventNotPublishedException;
import ru.practicum.main_svc.exception.ewm.NotFoundException.CommentNotFoundException;
import ru.practicum.main_svc.exception.ewm.NotFoundException.EventNotFoundException;
import ru.practicum.main_svc.exception.ewm.NotFoundException.UserNotFoundException;
import ru.practicum.main_svc.mapper.CommentMapper;
import ru.practicum.main_svc.model.Comment;
import ru.practicum.main_svc.model.Event;
import ru.practicum.main_svc.model.User;
import ru.practicum.main_svc.repository.CommentRepository;
import ru.practicum.main_svc.repository.EventRepository;
import ru.practicum.main_svc.repository.UserRepository;
import ru.practicum.main_svc.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventNotPublishedException("Event is not published yet");
        }

        Comment comment = commentMapper.toModel(newCommentDto);

        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setEvent(event);

        comment = commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        return commentMapper.toDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByUser(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created"));
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, pageable).getContent();
        return commentMapper.toDtoList(comments);
    }

    @Override
    public List<CommentDto> getCommentsByEvent(Long eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created"));
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageable).getContent();
        return commentMapper.toDtoList(comments);
    }

    @Override
    @Transactional
    public CommentDto updateComment(NewCommentDto newCommentDto, Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!comment.getAuthor().equals(user)) {
            throw new AccessDeniedException("Only own comments can be edited.");
        }
        comment.setText(newCommentDto.getText());

        comment = commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!comment.getAuthor().equals(user)) {
            throw new AccessDeniedException("Only own comments can be deleted.");
        }
        commentRepository.delete(comment);
    }
}
