
package com.productcatalog.repository;

import com.productcatalog.model.Category;
import com.productcatalog.model.Product;
import com.productcatalog.specification.ProductSpecification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Testcontainers
class ProductRepositoryTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("productcatalog_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name",
                postgres::getDriverClassName);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category electronics;

    @BeforeEach
    void setUp() {

        electronics = new Category(
                "Eletrônicos",
                "Produtos eletrônicos"
        );

        electronics = categoryRepository.save(electronics);

        saveProduct(
                "Teclado mecânico",
                "150.00",
                10
        );

        saveProduct(
                "Teclado de membrana",
                "50.00",
                25
        );

        saveProduct(
                "Mouse sem fio",
                "80.00",
                5
        );

        productRepository.flush();
    }

    private void saveProduct(
            String name,
            String price,
            int stock) {

        Product product = new Product();

        product.setName(name);
        product.setDescription("Produto para teste");
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(stock);
        product.setCategory(electronics);

        productRepository.save(product);
    }

    @Test
    void shouldFindProductsByNameIgnoringCase() {

        Specification<Product> specification =
                ProductSpecification.nameContains("TECLADO");

        Page<Product> result = productRepository.findAll(
                specification,
                PageRequest.of(0, 10)
        );

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void shouldCombineMultipleFilters() {

        Specification<Product> specification =
                Specification.where(
                        ProductSpecification.nameContains("teclado")
                )
                .and(ProductSpecification.hasCategory(electronics.getId()))
                .and(ProductSpecification.priceGreaterThanOrEqual(
                        new BigDecimal("100")
                ))
                .and(ProductSpecification.priceLessThanOrEqual(
                        new BigDecimal("200")
                ))
                .and(ProductSpecification.stockGreaterThanOrEqual(5))
                .and(ProductSpecification.stockLessThanOrEqual(15));

        Page<Product> result = productRepository.findAll(
                specification,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(
                "Teclado mecânico",
                result.getContent().get(0).getName()
        );
    }

    @Test
    void shouldReturnPaginatedProducts() {

        Page<Product> result = productRepository.findAll(
                PageRequest.of(0, 2)
        );

        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertTrue(result.isFirst());
    }

    @Test
    void shouldSortProductsByPriceAscending() {

        Page<Product> result = productRepository.findAll(
                PageRequest.of(
                        0,
                        10,
                        Sort.by(Sort.Direction.ASC, "price")
                )
        );

        assertEquals(
                new BigDecimal("50.00"),
                result.getContent().get(0).getPrice()
        );

        assertEquals(
                new BigDecimal("150.00"),
                result.getContent().get(2).getPrice()
        );
    }
}
