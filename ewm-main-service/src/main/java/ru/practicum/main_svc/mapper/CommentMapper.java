package ru.practicum.main_svc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main_svc.dto.comment.CommentDto;
import ru.practicum.main_svc.dto.comment.NewCommentDto;
import ru.practicum.main_svc.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toModel(NewCommentDto newCommentDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "eventId", source = "event.id")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDtoList(List<Comment> commentList);
}
