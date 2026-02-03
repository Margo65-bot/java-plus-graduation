package ru.practicum.api.compilation;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

@Validated
@RequestMapping("/admin/compilations")
public interface AdminCompilationApi {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto createCompilation(
            @RequestBody @Valid NewCompilationDto compilationDto
    );

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCompilation(
            @PathVariable("compId") long compilationId
    );

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    CompilationDto updateCompilation(
            @RequestBody @Valid UpdateCompilationRequest compilationUpdateDto,
            @PathVariable("compId") long compilationId
    );
}
