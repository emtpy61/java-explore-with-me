package ru.practicum.main_svc.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_svc.dto.compilation.CompilationDto;
import ru.practicum.main_svc.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationsApiController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable("compId") Long compId) {
        return compilationService.getCompilation(compId);
    }


    @GetMapping
    public List<CompilationDto> getCompilations(
            @Valid @RequestParam(value = "pinned", required = false) Boolean pinned,
            @Min(0) @Valid @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }
}
