package org.fitory.category.controller;

import lombok.RequiredArgsConstructor;
import org.fitory.category.dto.CategoryResponse;
import org.fitory.category.service.CategoryService;
import org.fitory.common.dto.PageResponse;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByCategoryId(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        return ResponseEntity.ok(productService.findAllByCategoryId(id, p, s));
    }
}
