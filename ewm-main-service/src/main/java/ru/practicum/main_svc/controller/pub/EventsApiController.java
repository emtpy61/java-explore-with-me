package ru.practicum.main_svc.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_svc.dto.event.EventFullDto;
import ru.practicum.main_svc.dto.event.EventShortDto;
import ru.practicum.main_svc.enums.SortValue;
import ru.practicum.main_svc.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventsApiController {
    private final EventService eventService;

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable("id") Long id,
            HttpServletRequest request) {

        return eventService.getEvent(id, request.getRequestURI(), request.getRemoteAddr());
    }


    @GetMapping
    public List<EventShortDto> getEvents(
            @Valid @RequestParam(value = "text", required = false) @Size(min = 1, max = 7000) String text,
            @Valid @RequestParam(value = "categories", required = false) List<Long> categories,
            @Valid @RequestParam(value = "paid", required = false) Boolean paid,
            @Valid @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @Valid @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @Valid @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false")
            Boolean onlyAvailable,
            @Valid @RequestParam(value = "sort", required = false) SortValue sort,
            @Valid @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {

        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                request.getRequestURI(), request.getRemoteAddr());
    }
}
