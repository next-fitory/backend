package org.fitory.product.dto;

public record UpdateProductRequest(Long categoryId,
                                    String name,
                                   String description,
                                   int price,
                                   int discountRate,
                                   int stock,
                                   String imageUrl
) {}
