package org.fitory.product.repository.impl;

import org.fitory.product.domain.Product;
import org.fitory.product.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            LocalDateTime now = LocalDateTime.now();
            Product saved = new Product(
                    idGenerator.getAndIncrement(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    false,
                    now,
                    now
            );
            store.put(saved.getId(), saved);
            return saved;
        }
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id))
                .filter(p -> !p.isDeleted());
    }

    @Override
    public List<Product> findAll() {
        return store.values().stream()
                .filter(p -> !p.isDeleted())
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        Product product = store.get(id);
        if (product != null) {
            product.delete();
        }
    }
}
