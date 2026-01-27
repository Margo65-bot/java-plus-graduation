package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.category.PublicCategoryApi;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryParam;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicCategoryController implements PublicCategoryApi {
    private final CategoryService categoryService;

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        log.info("Public: Method launched (findAll(Integer from = {}, Integer size = {}))", from, size);
        return categoryService.findAll(new CategoryParam(from, size));
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        log.info("Public: Method launched (deleteById(Long categoryId = {}))", catId);
        return categoryService.findById(catId);
    }
}
