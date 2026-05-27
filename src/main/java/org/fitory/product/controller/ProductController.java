package org.fitory.product.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.GetMapping;
import mvc.annotation.RequestMapping;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProduct() {
        return ResponseEntity.ok(productService.findAll());
    }


}
