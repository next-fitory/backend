package org.fitory.product.domain;

import lombok.*;
import org.fitory.product.dto.AddProductRequest;

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

    private String validateDescription(String description) {
        if (description.length() > 100) {
            throw new IllegalArgumentException("description must be less than 100 characters");
        }
        return description;
    }

    public int salePrice() {
        return (int) (this.price * (100 - this.discountRate) * 0.01);
    }

    public void delete() {
        this.deleted = true;
    }

    public void update(AddProductRequest requestDto) {
        this.name = requestDto.name();
        this.description = validateDescription(requestDto.description());
        this.price = requestDto.price();

        this.updatedAt = LocalDateTime.now();
    }
}
