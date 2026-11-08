
package com.productcatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productcatalog.dto.ProductRequestDTO;
import com.productcatalog.dto.ProductResponseDTO;
import com.productcatalog.exception.GlobalExceptionHandler;
import com.productcatalog.exception.ResourceNotFoundException;
import com.productcatalog.service.ProductService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductResponseDTO createProductResponse() {
        return new ProductResponseDTO(
                1L,
                "Teclado mecânico",
                "Teclado RGB",
                new BigDecimal("150.00"),
                10,
                null,
                LocalDateTime.now(),
                "teste.png"
        );
    }

    @Test
    void shouldReturnProductsWithStatus200() throws Exception {

        Page<ProductResponseDTO> page = new PageImpl<>(
                List.of(createProductResponse()),
                PageRequest.of(0, 10),
                1
        );

        when(productService.findAll(any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Teclado mecânico"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldCreateProductWithStatus201() throws Exception {

        ProductRequestDTO request = new ProductRequestDTO(
                "Teclado mecânico",
                "Teclado RGB",
                new BigDecimal("150.00"),
                10,
                1L
        );

        when(productService.create(any(ProductRequestDTO.class)))
                .thenReturn(createProductResponse());

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Teclado mecânico"));

        verify(productService).create(any(ProductRequestDTO.class));
    }

    @Test
    void shouldRejectProductWithNegativePrice() throws Exception {

        String invalidJson = """
                {
                    "name": "Teclado",
                    "description": "Teclado RGB",
                    "price": -10,
                    "stockQuantity": 10,
                    "categoryId": 1
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnProductById() throws Exception {

        when(productService.findById(1L))
                .thenReturn(createProductResponse());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Teclado mecânico"));
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {

        when(productService.findById(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Produto não encontrado: 999"
                ));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldDeleteProductWithStatus204() throws Exception {

        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).delete(1L);
    }

    @Test
    void shouldPassPaginationParametersToService() throws Exception {

        Page<ProductResponseDTO> page = new PageImpl<>(
                List.of(createProductResponse()),
                PageRequest.of(1, 5),
                6
        );

        when(productService.findAll(any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.totalElements").value(6));
    }
}
