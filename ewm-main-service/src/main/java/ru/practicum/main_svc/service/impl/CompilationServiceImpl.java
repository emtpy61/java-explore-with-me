package ru.practicum.main_svc.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_svc.dto.compilation.CompilationDto;
import ru.practicum.main_svc.dto.compilation.NewCompilationDto;
import ru.practicum.main_svc.dto.compilation.UpdateCompilationRequest;
import ru.practicum.main_svc.exception.ewm.NotFoundException.CompilationNotFoundException;
import ru.practicum.main_svc.mapper.CompilationMapper;
import ru.practicum.main_svc.model.Compilation;
import ru.practicum.main_svc.model.Event;
import ru.practicum.main_svc.repository.CompilationRepository;
import ru.practicum.main_svc.repository.EventRepository;
import ru.practicum.main_svc.service.CompilationService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        if (newCompilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            compilation.setEvents(new ArrayList<>(events));
        }
        compilation.setPinned(newCompilationDto.getPinned() != null && newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());

        compilation = compilationRepository.save(compilation);

        return compilationMapper.toDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));

        List<Long> eventsIds = updateCompilationRequest.getEvents();
        if (eventsIds != null) {
            List<Event> events = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(new ArrayList<>(events));
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        compilation = compilationRepository.save(compilation);

        return compilationMapper.toDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));
        return compilationMapper.toDto(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (pinned != null) {
            return compilationMapper.toDtoList(compilationRepository.findAllByPinned(pinned, page).toList());
        } else {
            return compilationMapper.toDtoList(compilationRepository.findAll(page).toList());
        }
    }
}
