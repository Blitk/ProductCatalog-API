package com.productcatalog.service;

import com.productcatalog.dto.*;
import com.productcatalog.exception.*;
import com.productcatalog.model.Category;
import com.productcatalog.repository.CategoryRepository;
import com.productcatalog.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO dto) {

        if (categoryRepository.existsByNameIgnoreCase(dto.name())) {
            throw new BusinessException("Category already exists");
        }

        Category category = new Category(
                dto.name(),
                dto.description()
        );

        return toDTO(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAll() {

        return categoryRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO findById(Long id) {

        Category category = getCategory(id);

        return toDTO(category);
    }

    @Transactional
    public CategoryResponseDTO update(
            Long id,
            CategoryRequestDTO dto) {

        Category category = getCategory(id);

        categoryRepository.findByNameIgnoreCase(dto.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Category name already exists"
                    );
                });

        category.setName(dto.name());
        category.setDescription(dto.description());

        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {

        Category category = getCategory(id);

        if (productRepository.existsByCategoryId(id)) {
            throw new BusinessException(
                    "Cannot delete a category that has products"
            );
        }

        categoryRepository.delete(category);
    }

    private Category getCategory(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: " + id
                        )
                );
    }

    private CategoryResponseDTO toDTO(Category category) {

        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
