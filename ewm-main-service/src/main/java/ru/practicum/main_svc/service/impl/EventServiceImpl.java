package ru.practicum.main_svc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_svc.dto.event.EventFullDto;
import ru.practicum.main_svc.dto.event.EventShortDto;
import ru.practicum.main_svc.dto.event.NewEventDto;
import ru.practicum.main_svc.dto.event.UpdateEventRequest;
import ru.practicum.main_svc.enums.EventState;
import ru.practicum.main_svc.enums.SortValue;
import ru.practicum.main_svc.enums.StateAction;
import ru.practicum.main_svc.exception.ewm.*;
import ru.practicum.main_svc.exception.ewm.NotFoundException.CategoryNotFoundException;
import ru.practicum.main_svc.exception.ewm.NotFoundException.EventNotFoundException;
import ru.practicum.main_svc.exception.ewm.NotFoundException.UserNotFoundException;
import ru.practicum.main_svc.mapper.EventMapper;
import ru.practicum.main_svc.model.Category;
import ru.practicum.main_svc.model.Event;
import ru.practicum.main_svc.model.QEvent;
import ru.practicum.main_svc.model.User;
import ru.practicum.main_svc.repository.CategoryRepository;
import ru.practicum.main_svc.repository.EventRepository;
import ru.practicum.main_svc.repository.RequestRepository;
import ru.practicum.main_svc.repository.UserRepository;
import ru.practicum.main_svc.service.EventService;
import ru.practicum.stat_client.StatisticClient;
import ru.practicum.stat_dto.EndpointHitDto;
import ru.practicum.stat_dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatisticClient statisticClient;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException(newEventDto.getCategory()));
        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongDateTimeException("The event can't be earlier than two hours from the current moment");
        }
        Event event = eventMapper.toModel(newEventDto);
        event.setCategory(category);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setPaid(newEventDto.getPaid() != null && newEventDto.getPaid());
        event.setRequestModeration(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration());
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Override
    public EventFullDto getEvent(Long id, String requestURI, String remoteAddr) {
        sendToStatisticServer(requestURI, remoteAddr);
        Event event = eventRepository.findByIdAndPublishedOnIsNotNull(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        getViewsFromStatisticServer(event);
        event.populateConfirmedRequests();
        return eventMapper.toDto(event);
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        event.populateConfirmedRequests();
        return eventMapper.toDto(event);
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
            String rangeEnd, Boolean onlyAvailable, SortValue sort, Integer from, Integer size,
            String requestURI, String remoteAddr) {

        sendToStatisticServer(requestURI, remoteAddr);
        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, dateFormatter) : null;
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, dateFormatter) : null;

        if (start != null && end != null && start.isAfter(end)) {
            throw new WrongDateTimeException("Start must be before end");
        }

        Pageable pageable;

        QEvent event = QEvent.event;

        Predicate predicate = event.isNotNull();

        if (text != null) {
            String searchText = "%" + text.toLowerCase() + "%";
            predicate = ExpressionUtils.and(predicate,
                    event.annotation.toLowerCase().like(searchText)
                            .or(event.description.toLowerCase().like(searchText))
            );
        }

        if (categories != null && !categories.isEmpty()) {
            predicate = ExpressionUtils.and(predicate, event.category.id.in(categories));
        }

        if (paid != null) {
            predicate = ExpressionUtils.and(predicate, event.paid.eq(paid));
        }

        if (start != null) {
            predicate = ExpressionUtils.and(predicate, event.eventDate.goe(start));
        }

        if (end != null) {
            predicate = ExpressionUtils.and(predicate, event.eventDate.loe(end));
        }
        Predicate availablePredicate = event.participantLimit.eq(0);
        if (onlyAvailable) {
            availablePredicate = ExpressionUtils.or(availablePredicate,
                    event.participantLimit.goe(event.requestList.size()));
            availablePredicate = ExpressionUtils.or(availablePredicate, event.requestModeration.eq(false));
            predicate = ExpressionUtils.and(predicate, availablePredicate);
        }
        if (sort != null && sort.equals(SortValue.EVENT_DATE)) {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate"));
        } else {
            pageable = PageRequest.of(from / size, size);
        }

        List<Event> events = eventRepository.findAll(predicate, pageable).getContent();

        if (sort != null && sort.equals(SortValue.VIEWS)) {
            events.sort(Comparator.comparing(Event::getViews));
        }
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        events.forEach(Event::populateConfirmedRequests);
        return eventMapper.toShortDtoList(events);
    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page).toList();
        events.forEach(Event::populateConfirmedRequests);
        return eventMapper.toShortDtoList(events);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
            String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime start = rangeStart != null ? LocalDateTime.parse(rangeStart, dateFormatter) : null;
        LocalDateTime end = rangeEnd != null ? LocalDateTime.parse(rangeEnd, dateFormatter) : null;

        if (start != null && end != null && start.isAfter(end)) {
            throw new WrongDateTimeException("Start must be before end");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        QEvent event = QEvent.event;

        Predicate predicate = event.isNotNull();

        if (categories != null && !categories.isEmpty()) {
            predicate = ExpressionUtils.and(predicate, event.category.id.in(categories));
        }

        if (users != null && !users.isEmpty()) {
            predicate = ExpressionUtils.and(predicate, event.initiator.id.in(users));
        }

        if (states != null && !states.isEmpty()) {
            predicate = ExpressionUtils.and(predicate, event.state.in(states));
        }

        if (start != null) {
            predicate = ExpressionUtils.and(predicate, event.eventDate.goe(start));
        }

        if (end != null) {
            predicate = ExpressionUtils.and(predicate, event.eventDate.loe(end));
        }

        List<Event> events = eventRepository.findAll(predicate, pageable).getContent();

        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        events.forEach(Event::populateConfirmedRequests);
        return eventMapper.toDtoList(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventRequest updateEventRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new AccessDeniedException("Only pending or canceled events can be changed");
        }
        if (updateEventRequest != null) {
            updateEventProperties(event, updateEventRequest);
            if (updateEventRequest.getStateAction() != null) {
                handleEventStateActionsByUser(event, updateEventRequest.getStateAction());
            }
        }
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest updateEventRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (updateEventRequest != null) {
            updateEventProperties(event, updateEventRequest);
            if (updateEventRequest.getStateAction() != null) {
                handleEventStateActionsByAdmin(event, updateEventRequest.getStateAction());
            }
        }

        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    private void updateEventProperties(Event event, UpdateEventRequest request) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategory()));
            event.setCategory(category);
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit().intValue());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        if (request.getEventDate() != null) {
            LocalDateTime eventDateTime = request.getEventDate();
            LocalDateTime publishedOn = event.getPublishedOn();

            if (isInvalidEventDate(eventDateTime, publishedOn)) {
                throw new WrongDateTimeException(
                        "The start date of the event to be modified is less than one hour from the publication date.");
            }

            event.setEventDate(eventDateTime);
        }
    }

    private boolean isInvalidEventDate(LocalDateTime eventDateTime, LocalDateTime publishedOn) {
        return eventDateTime.isBefore(LocalDateTime.now()) ||
                (publishedOn != null && eventDateTime.isBefore(publishedOn.plusHours(1)));
    }

    private void handleEventStateActionsByAdmin(Event event, StateAction stateAction) {
        if (event.getPublishedOn() != null) {
            throw new EventAlreadyPublishedException();
        }

        switch (stateAction) {
            case PUBLISH_EVENT:
                if (event.getState() == EventState.CANCELED) {
                    throw new EventAlreadyCanceledException();
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
            case REJECT_EVENT:
                event.setState(EventState.CANCELED);
                break;
            default:
                throw new IllegalStateActionException();
        }
    }

    private void handleEventStateActionsByUser(Event event, StateAction stateAction) {
        if (event.getPublishedOn() != null) {
            throw new EventAlreadyPublishedException();
        }

        switch (stateAction) {
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                break;
            case CANCEL_REVIEW:
                event.setState(EventState.CANCELED);
                break;
            default:
                throw new IllegalStateActionException();
        }
    }

    private void sendToStatisticServer(String requestURI, String remoteAddr) {

        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setTimestamp(LocalDateTime.now());
        endpointHitDto.setUri(requestURI);
        endpointHitDto.setApp("main-service");
        endpointHitDto.setIp(remoteAddr);
        System.out.println(endpointHitDto);
        statisticClient.addStats(endpointHitDto);
    }

    private void getViewsFromStatisticServer(Event event) {
        String uri = "/events/" + event.getId();
        List<ViewStatsDto> stats = statisticClient.getStats(event.getCreatedOn(),
                LocalDateTime.now(),
                List.of(uri),
                true);

        if (stats.size() == 1) {
            event.setViews(stats.get(0).getHits());
        } else {
            event.setViews(0L);
        }
    }
}
