package org.fitory.product.domain;

import lombok.*;
import org.fitory.product.dto.CreateProductRequest;
import org.fitory.product.dto.UpdateProductRequest;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public class Product {
    private Long id;
    private Long brandId;
    private Long categoryId;
    private String name;
    private String description;
    private int price;
    //    private int salePrice;
    private int discountRate;
    private int stock;
    private String imageUrl;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private static String validateDescription(String description) {
        if (description == null) {
            return "";
        }

        if (description.length() > 100) {
            throw new IllegalArgumentException("description must be less than 100 characters");
        }
        return description;
    }

    public int salePrice() {
        return (int) (this.price * (100 - this.discountRate) * 0.01);
    }

    public static Product create(CreateProductRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String validatedDescription = validateDescription(request.description());

        return Product.builder()
                .brandId(request.brandId())
                .categoryId(request.categoryId())
                .name(request.name())
                .description(validatedDescription)
                .price(request.price())
                .discountRate(request.discountRate())
                .stock(request.stock())
                .imageUrl(request.imageUrl())
                .deleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Product update(UpdateProductRequest request) {
        String validatedDescription = validateDescription(request.description());

        return this.toBuilder()
                .categoryId(request.categoryId())
                .name(request.name())
                .description(validatedDescription)
                .price(request.price())
                .discountRate(request.discountRate())
                .stock(request.stock())
                .imageUrl(request.imageUrl())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Product delete() {
        return this.toBuilder()
                .deleted(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}