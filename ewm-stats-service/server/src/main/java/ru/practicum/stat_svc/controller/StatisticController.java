package ru.practicum.stat_svc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat_dto.EndpointHitDto;
import ru.practicum.stat_dto.ViewStatsDto;
import ru.practicum.stat_svc.service.StatisticServiceImpl;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatisticController {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final StatisticServiceImpl statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEndpointHit(
            @Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("addEndpointHit: {}", endpointHitDto);
        statisticService.addEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam(value = "start") @DateTimeFormat(pattern = DATE_FORMAT) @Valid LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(pattern = DATE_FORMAT) @Valid LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return statisticService.getStatistic(start, end, uris, unique);
    }
}
