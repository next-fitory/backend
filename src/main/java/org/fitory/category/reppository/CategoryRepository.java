package org.fitory.category.reppository;

import org.fitory.category.domain.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> findAll();
}
