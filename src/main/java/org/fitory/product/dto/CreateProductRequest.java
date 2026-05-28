package org.fitory.product.dto;

public record CreateProductRequest(Long brandId,
                                   Long categoryId,
                                   String name,
                                   String description,
                                   int price,
                                   int discountRate,
                                   int stock,
                                   String imageUrl
) {}
