package com.productcatalog.controller;

import com.productcatalog.dto.ProductRequestDTO;
import com.productcatalog.dto.ProductResponseDTO;
import com.productcatalog.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDTO create(
            @Valid @RequestBody ProductRequestDTO dto) {

        return productService.create(dto);
    }

    @GetMapping
    public Page<ProductResponseDTO> findAll(
            @PageableDefault(
                 size = 10,
                 sort = "id",
                 direction = Sort.Direction.ASC
            ) Pageable pageable) {

        return productService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PutMapping("/{id}")
    public ProductResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto) {

        return productService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @GetMapping("/search")
    public Page<ProductResponseDTO> search(
            @RequestParam String name,
            Pageable pageable) {

        return productService.searchByName(name, pageable);
    }

    @GetMapping("/category/{categoryId}")
    public Page<ProductResponseDTO> findByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {

         return productService.findByCategory(categoryId, pageable);
        
    }

    
    @GetMapping("/filter")
    public Page<ProductResponseDTO> filter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return productService.filter(
                name,
                categoryId,
                minPrice,
                maxPrice,
                minStock,
                maxStock,
                pageable
        );
   }

    
}
