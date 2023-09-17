package ru.practicum.main_svc.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_svc.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.main_svc.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.main_svc.dto.request.ParticipationRequestDto;
import ru.practicum.main_svc.enums.EventState;
import ru.practicum.main_svc.enums.RequestStatus;
import ru.practicum.main_svc.exception.ewm.*;
import ru.practicum.main_svc.exception.ewm.NotFoundException.EventNotFoundException;
import ru.practicum.main_svc.exception.ewm.NotFoundException.RequestNotFoundException;
import ru.practicum.main_svc.exception.ewm.NotFoundException.UserNotFoundException;
import ru.practicum.main_svc.mapper.RequestMapper;
import ru.practicum.main_svc.model.Event;
import ru.practicum.main_svc.model.Request;
import ru.practicum.main_svc.model.User;
import ru.practicum.main_svc.repository.EventRepository;
import ru.practicum.main_svc.repository.RequestRepository;
import ru.practicum.main_svc.repository.UserRepository;
import ru.practicum.main_svc.service.RequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new RequestAlreadyExistException();
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (event.getInitiator().getId().equals(userId)) {
            throw new AccessDeniedException("Unable to create a request by the initiator");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventNotPublishedException("Event is not published yet");
        }
        int confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int participationLimit = event.getParticipantLimit();

        if (confirmedRequests >= participationLimit && participationLimit != 0) {
            throw new ParticipantLimitException("Member limit exceeded ");
        }

        long requests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        Request request = new Request();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            event = eventRepository.save(event);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);

        request = requestRepository.save(request);
        return requestMapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return requestMapper.toDtoList(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
        return requestMapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId) {
        return requestMapper.toDtoList(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        RequestStatus requestStatus = eventRequestStatusUpdateRequest.getStatus();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new AccessDeniedException("Unable to update request status by a non-initiator");
        }

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return eventRequestStatusUpdateResult;
        }

        List<Request> requests = requestRepository.findAllByEventIdAndIdIn(eventId,
                eventRequestStatusUpdateRequest.getRequestIds());

        for (Request request : requests) {
            if (request.getStatus().equals(RequestStatus.REJECTED)) {
                throw new WrongRequestStatusException("Request must have status PENDING");
            }
            switch (requestStatus) {
                case CONFIRMED:
                    if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                        throw new EventParticipantLimitException("The participant limit has been reached");
                    }
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(request);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    break;
                case REJECTED:
                    if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                        throw new WrongRequestStatusException("Request must have status PENDING");
                    }
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(request);
                    break;
            }
        }
        eventRequestStatusUpdateResult.setConfirmedRequests(requestMapper.toDtoList(confirmedRequests));
        eventRequestStatusUpdateResult.setRejectedRequests(requestMapper.toDtoList(rejectedRequests));
        requestRepository.saveAll(requests);
        eventRepository.save(event);
        return eventRequestStatusUpdateResult;
    }
}
