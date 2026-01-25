package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.category.AdminCategoryApi;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.category.CategoryDto;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminCategoryController implements AdminCategoryApi {
    private final CategoryService categoryService;

    @Override
    public CategoryDto createCategory(CategoryDto category) {
        log.info("Admin: Method launched (save(CategoryDto category = {}))", category);
        return categoryService.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        log.info("Admin: Method launched (delete(Long categoryId = {}))", categoryId);
        categoryService.delete(categoryId);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto category) {
        log.info("Admin: Method launched (update(Long categoryId = {}, CategoryDto category = {}))", categoryId, category);
        return categoryService.update(categoryId, category);
    }
}
