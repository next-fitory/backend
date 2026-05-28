package org.fitory.product.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.GetMapping;
import mvc.annotation.RequestMapping;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.service.ProductCurationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductCurationController {
    private final ProductCurationService productCurationService;

    @GetMapping("/new-arrivals")
    public ResponseEntity<List<ProductResponse>> newArrivals() {
        return ResponseEntity.ok(productCurationService.findNewArrivals());
    }

    @GetMapping("/ranks")
    public ResponseEntity<List<ProductResponse>> ranked() {
        return ResponseEntity.ok(productCurationService.findRanked());
    }
}
