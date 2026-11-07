
package com.productcatalog.service;

import com.productcatalog.dto.CategoryRequestDTO;
import com.productcatalog.exception.BusinessException;
import com.productcatalog.exception.ResourceNotFoundException;
import com.productcatalog.model.Category;
import com.productcatalog.repository.CategoryRepository;
import com.productcatalog.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        productRepository = mock(ProductRepository.class);

        categoryService = new CategoryService(
                categoryRepository,
                productRepository
        );
    }

    @Test
    void shouldCreateCategorySuccessfully() {

        CategoryRequestDTO request = new CategoryRequestDTO(
                "Eletrônicos",
                "Produtos eletrônicos"
        );

        Category savedCategory = mock(Category.class);

        when(categoryRepository.existsByNameIgnoreCase("Eletrônicos"))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);

        when(savedCategory.getId()).thenReturn(1L);
        when(savedCategory.getName()).thenReturn("Eletrônicos");
        when(savedCategory.getDescription())
                .thenReturn("Produtos eletrônicos");

        var result = categoryService.create(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Eletrônicos", result.name());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldRejectDuplicateCategoryName() {

        CategoryRequestDTO request = new CategoryRequestDTO(
                "Eletrônicos",
                "Outra descrição"
        );

        when(categoryRepository.existsByNameIgnoreCase("Eletrônicos"))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> categoryService.create(request)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void shouldNotDeleteCategoryThatHasProducts() {

        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(mock(Category.class)));

        when(productRepository.existsByCategoryId(categoryId))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> categoryService.delete(categoryId)
        );

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void shouldDeleteCategoryWithoutProducts() {

        Long categoryId = 1L;

        Category category = mock(Category.class);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.existsByCategoryId(categoryId))
                .thenReturn(false);

        categoryService.delete(categoryId);

        verify(categoryRepository).delete(category);
    }

    @Test
    void shouldThrowExceptionWhenCategoryDoesNotExist() {

        Long categoryId = 999L;

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.findById(categoryId)
        );
    }
}
