package ru.practicum.api.category;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;

@Validated
@RequestMapping("/categories")
public interface PublicCategoryApi {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    );

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    CategoryDto getCategoryById(
            @PathVariable Long catId
    );
}
