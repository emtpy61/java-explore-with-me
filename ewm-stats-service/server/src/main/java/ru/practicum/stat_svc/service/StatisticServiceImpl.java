package ru.practicum.stat_svc.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stat_dto.EndpointHitDto;
import ru.practicum.stat_dto.ViewStatsDto;
import ru.practicum.stat_svc.exception.WrongDateTimeException;
import ru.practicum.stat_svc.mapper.EndpointHitMapper;
import ru.practicum.stat_svc.mapper.ViewStatsMapper;
import ru.practicum.stat_svc.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class StatisticServiceImpl implements StatisticService {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatisticRepository statisticRepository;
    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;

    @Override
    @Transactional
    public void addEndpointHit(EndpointHitDto endpointHitDto) {
        log.debug("Save hit: " + endpointHitDto);
        statisticRepository.save(endpointHitMapper.toModel(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start == null || end == null || start.isAfter(end)) {
            throw new WrongDateTimeException("Start and end must be provided and Start must be before end");
        }
        return unique ? viewStatsMapper.toModel(statisticRepository.getStatsUniqueIp(start, end, uris))
                : viewStatsMapper.toModel(statisticRepository.getStats(start, end, uris));
    }
}
