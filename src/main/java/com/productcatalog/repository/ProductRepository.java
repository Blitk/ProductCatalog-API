
package com.productcatalog.repository;

import com.productcatalog.dto.ProductResponseDTO;
import com.productcatalog.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends
        JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    boolean existsByCategoryId(Long categoryId);

	Page<ProductResponseDTO> findByCategoryId(Long categoryId, Pageable pageable);

	Page<ProductResponseDTO> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
