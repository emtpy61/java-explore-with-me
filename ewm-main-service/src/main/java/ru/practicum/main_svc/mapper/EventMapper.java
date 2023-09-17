package ru.practicum.main_svc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main_svc.dto.event.EventFullDto;
import ru.practicum.main_svc.dto.event.EventShortDto;
import ru.practicum.main_svc.dto.event.NewEventDto;
import ru.practicum.main_svc.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "category", target = "category.id")
    Event toModel(NewEventDto newEventDto);

    EventFullDto toDto(Event event);

    List<EventFullDto> toDtoList(List<Event> events);

    List<EventShortDto> toShortDtoList(List<Event> events);
}
