package ru.practicum.stat_svc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat_dto.EndpointHitDto;
import ru.practicum.stat_dto.ViewStatsDto;
import ru.practicum.stat_svc.service.StatisticServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatisticController {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final StatisticServiceImpl statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEndpointHit(
            @RequestBody EndpointHitDto endpointHitDto) {
        log.info("addEndpointHit: {}", endpointHitDto);
        statisticService.addEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @DateTimeFormat(pattern = DATE_FORMAT) @RequestParam(value = "start") LocalDateTime start,
            @DateTimeFormat(pattern = DATE_FORMAT) @RequestParam(value = "end") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return statisticService.getStatistic(start, end, uris, unique);
    }
}
