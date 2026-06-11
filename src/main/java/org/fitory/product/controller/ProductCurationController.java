package org.fitory.product.controller;

import lombok.RequiredArgsConstructor;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.service.ProductCurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
