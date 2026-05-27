package org.fitory.category.dto;

import lombok.Builder;
import org.fitory.category.domain.Category;

@Builder
public record CategoryResponse(Long id, String label, String emoji, String slug, Long sortOrder) {
    public static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .label(category.getLabel())
                .emoji(category.getEmoji())
                .slug(category.getSlug())
                .sortOrder(category.getSortOrder())
                .build();
    }
}
