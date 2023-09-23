package ru.practicum.main_svc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_svc.dto.event.EventFullDto;
import ru.practicum.main_svc.dto.event.EventShortDto;
import ru.practicum.main_svc.dto.event.NewEventDto;
import ru.practicum.main_svc.dto.event.UpdateEventRequest;
import ru.practicum.main_svc.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.main_svc.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.main_svc.dto.request.ParticipationRequestDto;
import ru.practicum.main_svc.service.EventService;
import ru.practicum.main_svc.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UsersApiController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable("userId") Long userId,
            @Valid @RequestBody NewEventDto body) {
        return eventService.createEvent(userId, body);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(
            @PathVariable("userId") Long userId,
            @Valid @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody UpdateEventRequest body) {
        return eventService.updateEventByUser(userId, eventId, body);
    }


    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(
            @PathVariable("userId") Long userId,
            @NotNull @Valid @RequestParam(value = "eventId", required = true) Long eventId) {
        ParticipationRequestDto response = requestService.createRequest(userId, eventId);
        return response;
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable("userId") Long userId) {
        return requestService.getRequestsByUser(userId);
    }


    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") Long userId,
            @PathVariable("requestId") Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }


    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest body) {
        return requestService.updateRequest(userId, eventId, body);
    }


    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipants(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {
        return requestService.getRequestsByEvent(userId, eventId);
    }
}
