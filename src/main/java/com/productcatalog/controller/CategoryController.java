package com.productcatalog.controller;

import com.productcatalog.dto.CategoryRequestDTO;
import com.productcatalog.dto.CategoryResponseDTO;
import com.productcatalog.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDTO create(
            @Valid @RequestBody CategoryRequestDTO dto) {

        return categoryService.create(dto);
    }

    @GetMapping
    public List<CategoryResponseDTO> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @PutMapping("/{id}")
    public CategoryResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO dto) {

        return categoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
