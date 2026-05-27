package org.fitory.product.dto;

public record AddProductRequest(Long brandId,
                                Long categoryId,
                                String name,
                                String description,
                                int price,
                                int discountRate,
                                int stock,
                                String imageUrl
) {}
