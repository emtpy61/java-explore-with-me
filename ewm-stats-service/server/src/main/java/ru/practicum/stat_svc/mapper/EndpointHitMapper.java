package ru.practicum.stat_svc.mapper;

import lombok.Generated;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stat_dto.EndpointHitDto;
import ru.practicum.stat_svc.model.EndpointHit;

@Generated
@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    @Mapping(target = "timestamp", source = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHit toModel(EndpointHitDto endpointHitDto);
}
