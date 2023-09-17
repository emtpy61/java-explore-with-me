package ru.practicum.main_svc.service;

import ru.practicum.main_svc.dto.compilation.CompilationDto;
import ru.practicum.main_svc.dto.compilation.NewCompilationDto;
import ru.practicum.main_svc.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

    void deleteCompilation(Long compId);

    CompilationDto getCompilation(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);
}
