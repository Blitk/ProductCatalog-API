package com.productcatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Name must have at most 100 characters")
    String name,

    @Size(max = 255)
    String description

) {}
