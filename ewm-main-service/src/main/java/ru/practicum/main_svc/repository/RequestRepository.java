package ru.practicum.main_svc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main_svc.enums.RequestStatus;
import ru.practicum.main_svc.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByRequesterId(Long userId);

    Optional<Request> findByRequesterIdAndId(Long userId, Long requestId);

    List<Request> findAllByEventIdAndIdIn(Long eventId, List<Long> requestIds);

    int countByEventIdAndStatus(Long eventId, RequestStatus requestStatus);
}
