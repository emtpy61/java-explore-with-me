package ru.practicum.stat_svc.service;


import ru.practicum.stat_dto.EndpointHitDto;
import ru.practicum.stat_dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    void addEndpointHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
