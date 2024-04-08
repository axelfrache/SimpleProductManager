package io.github.axelfrache.productmanager.service;

import io.github.axelfrache.productmanager.model.Category;
import io.github.axelfrache.productmanager.repository.CategoryRepository;
import io.github.axelfrache.productmanager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) { // Modifier le constructeur pour injecter ProductService
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public Optional<Category> findById(Long id) {
        return this.categoryRepository.findById(id);
    }

    public void save(Category category) {
        this.categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        Category category = this.categoryRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Category not found with ID: " + id));

        category.getProducts().forEach(product -> {
            product.setCategory(null);
            productRepository.save(product);
        });

        this.categoryRepository.delete(category);
    }

    public Iterable<Category> findAll() {
        return this.categoryRepository.findAll();
    }
}
