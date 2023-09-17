package ru.practicum.main_svc.service;

import ru.practicum.main_svc.dto.event.EventFullDto;
import ru.practicum.main_svc.dto.event.EventShortDto;
import ru.practicum.main_svc.dto.event.NewEventDto;
import ru.practicum.main_svc.dto.event.UpdateEventRequest;
import ru.practicum.main_svc.enums.EventState;
import ru.practicum.main_svc.enums.SortValue;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long id, HttpServletRequest request);

    EventFullDto getEventByUser(Long userId, Long eventId);

    List<EventShortDto> getEvents(String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            SortValue sort,
            Integer from,
            Integer size,
            HttpServletRequest request);

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    List<EventFullDto> getEventsByAdmin(List<Long> users,
            List<EventState> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventRequest updateEventRequest);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest updateEventRequest);
}
