package ru.practicum.main_svc.service;

import ru.practicum.main_svc.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.main_svc.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.main_svc.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUser(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequest(Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
