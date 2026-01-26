package ru.practicum.api.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.category.CategoryDto;

@Validated
@RequestMapping("/admin/categories")
public interface AdminCategoryApi {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto createCategory(
            @RequestBody @Valid CategoryDto category
    );

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(
            @PathVariable @Positive Long catId
    );

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    CategoryDto updateCategory(
            @PathVariable @Positive Long catId,
            @Valid @RequestBody CategoryDto category
    );
}
