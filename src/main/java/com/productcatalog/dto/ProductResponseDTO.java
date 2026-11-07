package com.productcatalog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponseDTO(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stockQuantity,
    CategoryResponseDTO category,
    LocalDateTime createdAt
) {}
