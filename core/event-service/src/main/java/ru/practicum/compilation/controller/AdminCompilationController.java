package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.compilation.AdminCompilationApi;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminCompilationController implements AdminCompilationApi {
    private final CompilationService compilationService;

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        log.info("Admin: creating new compilation title={}", compilationDto.getTitle());
        return compilationService.createCompilation(compilationDto);
    }

    @Override
    public void deleteCompilation(long compilationId) {
        log.info("Admin: deleting compilation id={}", compilationId);
        compilationService.deleteCompilation(compilationId);
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest compilationUpdateDto, long compilationId) {
        log.info("Admin: updating compilation id={}", compilationId);
        return compilationService.updateCompilation(compilationId, compilationUpdateDto);
    }
}
