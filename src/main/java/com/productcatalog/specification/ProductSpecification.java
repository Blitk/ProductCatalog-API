
package com.productcatalog.specification;

import com.productcatalog.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> nameContains(String name) {
        return (root, query, builder) ->
                name == null || name.isBlank()
                        ? null
                        : builder.like(
                                builder.lower(root.get("name")),
                                "%" + name.toLowerCase() + "%"
                        );
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, builder) ->
                categoryId == null
                        ? null
                        : builder.equal(
                                root.get("category").get("id"),
                                categoryId
                        );
    }

    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, builder) ->
                minPrice == null
                        ? null
                        : builder.greaterThanOrEqualTo(
                                root.get("price"),
                                minPrice
                        );
    }

    public static Specification<Product> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, builder) ->
                maxPrice == null
                        ? null
                        : builder.lessThanOrEqualTo(
                                root.get("price"),
                                maxPrice
                        );
    }

    public static Specification<Product> stockGreaterThanOrEqual(Integer minStock) {
        return (root, query, builder) ->
                minStock == null
                        ? null
                        : builder.greaterThanOrEqualTo(
                                root.get("stockQuantity"),
                                minStock
                        );
    }

    public static Specification<Product> stockLessThanOrEqual(Integer maxStock) {
        return (root, query, builder) ->
                maxStock == null
                        ? null
                        : builder.lessThanOrEqualTo(
                                root.get("stockQuantity"),
                                maxStock
                        );
    }
}
