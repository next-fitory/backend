package org.fitory.category.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Category {
    private Long id;
    private String label;
    private String emoji;
    private String slug;
    private Long sortOrder;
}
