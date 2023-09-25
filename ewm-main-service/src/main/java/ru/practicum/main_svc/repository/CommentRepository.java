package ru.practicum.main_svc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main_svc.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByAuthorId(Long authorId, Pageable page);

    Page<Comment> findAllByEventId(Long eventId, Pageable page);
}
