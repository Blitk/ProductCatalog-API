package com.productcatalog.service;

import com.productcatalog.dto.*;
import com.productcatalog.exception.*;
import com.productcatalog.model.*;
import com.productcatalog.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {

        Category category = getCategory(dto.categoryId());

        Product product = new Product();

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());
        product.setCategory(category);

        return toDTO(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {

        return productRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {

        return toDTO(getProduct(id));
    }

    @Transactional
    public ProductResponseDTO update(
            Long id,
            ProductRequestDTO dto) {

        Product product = getProduct(id);
        Category category = getCategory(dto.categoryId());

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());
        product.setCategory(category);

        return toDTO(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {

        Product product = getProduct(id);

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchByName(String name) {

        return productRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByCategory(Long categoryId) {

        getCategory(categoryId);

        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private Product getProduct(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found: " + id
                        )
                );
    }

    private Category getCategory(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: " + id
                        )
                );
    }

    private ProductResponseDTO toDTO(Product product) {

        Category category = product.getCategory();

        CategoryResponseDTO categoryDTO =
                new CategoryResponseDTO(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                );

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                categoryDTO,
                product.getCreatedAt()
        );
    }
}
