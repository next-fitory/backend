package org.fitory.brand.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.brand.dto.CreateBrandRequest;
import org.fitory.brand.dto.BrandResponse;
import org.fitory.brand.dto.UpdateBrandRequest;
import org.fitory.brand.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAll() {
        List<BrandResponse> response = brandService.findAll().stream()
                .map(BrandResponse::of)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(BrandResponse.of(brandService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BrandResponse> create(@RequestBody CreateBrandRequest request) {
        return ResponseEntity.created(BrandResponse.of(brandService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> update(@PathVariable Long id,
                                                @RequestBody UpdateBrandRequest request) {
        return ResponseEntity.ok(BrandResponse.of(brandService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent();
    }
}
