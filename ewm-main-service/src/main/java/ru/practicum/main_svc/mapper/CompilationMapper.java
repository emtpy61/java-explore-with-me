package ru.practicum.main_svc.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main_svc.dto.compilation.CompilationDto;
import ru.practicum.main_svc.model.Compilation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationDto toDto(Compilation compilation);

    List<CompilationDto> toDtoList(List<Compilation> compilations);
}