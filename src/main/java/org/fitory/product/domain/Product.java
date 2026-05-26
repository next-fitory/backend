package org.fitory.product.domain;

import lombok.*;
import org.fitory.product.dto.RequestDto;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public class Product {
    private Long id;
    private String name;
    private String description;

    @Setter
    private int price;

    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Product(Long id, String name, String description, int price, boolean deleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = validateDescription(description);
        this.price = price;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private String validateDescription(String description) {
        if (description.length() > 100) {
            throw new IllegalArgumentException("description must be less than 100 characters");
        }
        return description;
    }

    public void delete() {
        this.deleted = true;
    }

    public void update(RequestDto requestDto) {
        this.name = requestDto.name();
        this.description = requestDto.description();
        this.price = requestDto.price();

        this.updatedAt = LocalDateTime.now();
    }
}
