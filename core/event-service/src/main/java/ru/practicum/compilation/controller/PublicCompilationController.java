package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.compilation.PublicCompilationApi;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.dto.compilation.CompilationDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicCompilationController implements PublicCompilationApi {
    private final CompilationService compilationService;

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationService.findCompilationsByParam(pinned, pageable);
    }

    @Override
    public CompilationDto getCompilationById(long compilationId) {
        log.info("Public: get compilation id={}", compilationId);
        return compilationService.findCompilationById(compilationId);
    }
}
