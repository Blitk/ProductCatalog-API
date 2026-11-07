
package com.productcatalog.service;

import com.productcatalog.exception.InvalidFilterException;
import com.productcatalog.repository.CategoryRepository;
import com.productcatalog.repository.ProductRepository;
import com.productcatalog.specification.ProductSpecification;
import com.productcatalog.model.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        categoryRepository = mock(CategoryRepository.class);

        productService = new ProductService(
                productRepository,
                categoryRepository
        );
    }

    static Stream<Arguments> invalidFilters() {
        return Stream.of(
                Arguments.of(null, -1L, null, null, null, null),
                Arguments.of(null, null, new BigDecimal("-10"), null, null, null),
                Arguments.of(null, null, null, new BigDecimal("-5"), null, null),
                Arguments.of(null, null, new BigDecimal("300"),
                        new BigDecimal("50"), null, null),
                Arguments.of(null, null, null, null, -1, null),
                Arguments.of(null, null, null, null, null, -1),
                Arguments.of(null, null, null, null, 20, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidFilters")
    void shouldRejectInvalidFilters(
            String name,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minStock,
            Integer maxStock) {

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(
                InvalidFilterException.class,
                () -> productService.filter(
                        name,
                        categoryId,
                        minPrice,
                        maxPrice,
                        minStock,
                        maxStock,
                        pageable
                )
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldReturnEmptyPageWhenNoProductsMatch() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> emptyPage = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(productRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(emptyPage);

        Page<?> result = productService.filter(
                "teclado",
                null,
                new BigDecimal("50"),
                new BigDecimal("300"),
                null,
                null,
                pageable
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(productRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );
    }

    @Test
    void shouldReturnNotFoundWhenCategoryDoesNotExist() {

        Pageable pageable = PageRequest.of(0, 10);

        when(categoryRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                com.productcatalog.exception.ResourceNotFoundException.class,
                () -> productService.filter(
                        null,
                        999L,
                        null,
                        null,
                        null,
                        null,
                        pageable
                )
        );

        verifyNoInteractions(productRepository);
    }
}
