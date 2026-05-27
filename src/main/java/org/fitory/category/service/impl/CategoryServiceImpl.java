package org.fitory.category.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.category.dto.CategoryResponse;
import org.fitory.category.reppository.CategoryRepository;
import org.fitory.category.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(CategoryResponse::of).toList();
    }
}
