package org.fitory.category.repository;

import org.fitory.category.domain.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> findAll();
}
