package org.fitory.brand.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.brand.dto.CreateBrandRequest;
import org.fitory.brand.dto.CreateBrandResponse;
import org.fitory.brand.dto.UpdateBrandRequest;
import org.fitory.brand.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<CreateBrandResponse>> getAll() {
        List<CreateBrandResponse> response = brandService.findAll().stream()
                .map(CreateBrandResponse::of)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreateBrandResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(CreateBrandResponse.of(brandService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<CreateBrandResponse> create(@RequestBody CreateBrandRequest request) {
        return ResponseEntity.created(CreateBrandResponse.of(brandService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateBrandResponse> update(@PathVariable Long id,
                                                      @RequestBody UpdateBrandRequest request) {
        return ResponseEntity.ok(CreateBrandResponse.of(brandService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent();
    }
}
