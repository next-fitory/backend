package org.fitory.product.dto;

public record ProductSearchRequest(
        String categorySlug,
        String brandName,
        String keyword
) {}
