package org.fitory.cart.dto;

import lombok.Builder;
import org.fitory.product.dto.ProductResponse;

@Builder
public record CartProductResponse(
        ProductResponse product,
        int quantity,
        String createdAt,
        String updatedAt
) {}
