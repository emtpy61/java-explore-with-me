package ru.practicum.stat_svc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stat_svc.model.EndpointHit;
import ru.practicum.stat_svc.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "select new ru.practicum.stat_svc.model.ViewStats(eh.app, eh.uri, count(eh.ip)) " +
            "from  EndpointHit eh " +
            "where eh.timestamp between :start and :end " +
            "and ((:uris) is null or eh.uri in :uris) " +
            "group by eh.app, eh.uri " +
            "order by count(eh.ip) desc")
    List<ViewStats> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);

    @Query(value = "select new ru.practicum.stat_svc.model.ViewStats(eh.app, eh.uri, count(distinct eh.ip) as hits) " +
            "from  EndpointHit eh " +
            "where eh.timestamp between :start and :end " +
            "and ((:uris) is null or eh.uri in :uris) " +
            "group by eh.app, eh.uri " +
            "order by hits desc")
    List<ViewStats> getStatsUniqueIp(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);
}

