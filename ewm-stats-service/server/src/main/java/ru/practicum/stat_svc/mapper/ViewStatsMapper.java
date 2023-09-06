package ru.practicum.stat_svc.mapper;

import lombok.Generated;
import org.mapstruct.Mapper;
import ru.practicum.stat_dto.ViewStatsDto;
import ru.practicum.stat_svc.model.ViewStats;

import java.util.List;

@Generated
@Mapper(componentModel = "spring")
public interface ViewStatsMapper {
    List<ViewStatsDto> toModel(List<ViewStats> viewStats);
}
