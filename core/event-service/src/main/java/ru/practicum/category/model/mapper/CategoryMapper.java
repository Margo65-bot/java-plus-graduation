package ru.practicum.category.model.mapper;

import ru.practicum.category.model.Category;
import ru.practicum.dto.category.CategoryDto;

public class CategoryMapper {
    public static Category mapToCategory(CategoryDto dto) {
        return Category.builder()
                .name(dto.name())
                .build();
    }

    public static CategoryDto mapToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
