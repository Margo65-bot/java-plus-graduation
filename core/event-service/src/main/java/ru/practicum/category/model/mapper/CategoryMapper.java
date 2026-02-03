package ru.practicum.category.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.category.model.Category;
import ru.practicum.dto.category.CategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category mapToCategory(CategoryDto dto);

    CategoryDto mapToCategoryDto(Category category);
}