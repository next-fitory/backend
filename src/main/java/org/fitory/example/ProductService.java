package org.fitory.example;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product create(ProductCreateRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (request.price() < 0) {
            throw new IllegalArgumentException("Product price must not be negative");
        }
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .build();
        return productRepository.save(product);
    }

    public Product update(Long id, ProductUpdateRequest request) {
        Product product = findById(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        return productRepository.save(product);
    }

    public void delete(Long id) {
        if (!productRepository.deleteById(id)) {
            throw new ProductNotFoundException(id);
        }
    }
}
