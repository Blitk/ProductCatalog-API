package com.productcatalog.service;

import com.productcatalog.dto.*;
import com.productcatalog.exception.*;
import com.productcatalog.model.*;
import com.productcatalog.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.productcatalog.specification.ProductSpecification;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.List;
import com.productcatalog.exception.InvalidFilterException;

@Service
public class ProductService {

	private final ProductImageService productImageService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
		    ProductImageService productImageService,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
		this.productImageService = productImageService;

    }

	@Transactional
    public ProductResponseDTO uploadImage(Long productId, MultipartFile file) {

        Product product = getProduct(productId);

        String oldImageUrl = product.getImageUrl();

        String newImageUrl = productImageService.store(file);

        product.setImageUrl(newImageUrl);

        Product updatedProduct = productRepository.save(product);

        // Remove a imagem anterior após salvar a nova.
        if (oldImageUrl != null) {
            productImageService.delete(oldImageUrl);
        }

        return toDTO(updatedProduct);
    }

    @Transactional
    public ProductResponseDTO deleteImage(Long productId) {

        Product product = getProduct(productId);

        String imageUrl = product.getImageUrl();

        if (imageUrl == null) {
            throw new ResourceNotFoundException(
                    "Este produto não possui imagem."
            );
        }

        product.setImageUrl(null);

        Product updatedProduct = productRepository.save(product);

        productImageService.delete(imageUrl);

        return toDTO(updatedProduct);
    }
	
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> filter(
            String name,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minStock,
            Integer maxStock,
            Pageable pageable) {

     
		validateFilters(

            categoryId,
            minPrice,
            maxPrice,
            minStock,
            maxStock
        );

        if (categoryId != null && !categoryRepository.existsById(categoryId)) {

             throw new ResourceNotFoundException(

                     "Categoria não encontrada: " + categoryId
             );
        }
		
        Specification<Product> specification =
                Specification.where(ProductSpecification.nameContains(name))
                        .and(ProductSpecification.hasCategory(categoryId))
                        .and(ProductSpecification.priceGreaterThanOrEqual(minPrice))
                        .and(ProductSpecification.priceLessThanOrEqual(maxPrice))
                        .and(ProductSpecification.stockGreaterThanOrEqual(minStock))
                        .and(ProductSpecification.stockLessThanOrEqual(maxStock));

        return productRepository.findAll(specification, pageable)
                .map(this::toDTO);
    }


    
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAll(Pageable pageable) {

         return productRepository.findAll(pageable)
                 .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findByCategory(
            Long categoryId,
            Pageable pageable) {

         getCategory(categoryId);

         return productRepository
                 .findByCategoryId(categoryId, pageable)
                 .map(this::toDTO);
    }

    
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchByName(
            String name,
            Pageable pageable) {

         return productRepository
                 .findByNameContainingIgnoreCase(name, pageable)
                 .map(this::toDTO);
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

    
   private void validateFilters(
           Long categoryId,
           BigDecimal minPrice,
           BigDecimal maxPrice,
           Integer minStock,
           Integer maxStock) {

        if (categoryId != null && categoryId <= 0) {
            throw new InvalidFilterException(
                    "O ID da categoria deve ser positivo."
            );
        }

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
             throw new InvalidFilterException(
                     "O preço mínimo não pode ser negativo."
             );
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
             throw new InvalidFilterException(
                     "O preço máximo não pode ser negativo."
             );
         }

         if (minPrice != null && maxPrice != null
                 && minPrice.compareTo(maxPrice) > 0) {
             throw new InvalidFilterException(
                     "O preço mínimo não pode ser maior que o preço máximo."
             );
         }

        if (minStock != null && minStock < 0) {
             throw new InvalidFilterException(
                     "O estoque mínimo não pode ser negativo."
             );
        }

        if (maxStock != null && maxStock < 0) {
             throw new InvalidFilterException(
                     "O estoque máximo não pode ser negativo."
             );
        }

        if (minStock != null && maxStock != null
                 && minStock > maxStock) {
             throw new InvalidFilterException(
                     "O estoque mínimo não pode ser maior que o estoque máximo."
             );
        }
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
                product.getCreatedAt(),
			    product.getImageUrl()
        );
    }
}
