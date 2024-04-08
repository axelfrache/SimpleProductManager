package io.github.axelfrache.productmanager.service;

import io.github.axelfrache.productmanager.model.Product;
import io.github.axelfrache.productmanager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findById(Long id) {
        return this.productRepository.findById(id).orElse(null);
    }

    public void save(Product product) {
        this.productRepository.save(product);
    }

    public void deleteById(Long id) {
        this.productRepository.deleteById(id);
    }

    public Iterable<Product> findAll() {
        return this.productRepository.findAll();
    }

    public void update(Product product) {
        if (product.getId() != null) {
            this.productRepository.save(product);
        }
    }
}