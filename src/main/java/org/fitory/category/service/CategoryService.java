package org.fitory.category.service;

import org.fitory.category.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> findAll();
}
