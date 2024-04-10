package io.github.axelfrache.productmanager.service;

import io.github.axelfrache.productmanager.model.Product;
import io.github.axelfrache.productmanager.repository.CommandRepository;
import io.github.axelfrache.productmanager.model.Command;
import io.github.axelfrache.productmanager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CommandRepository commandRepository;


    @Autowired
    public ProductService(ProductRepository productRepository, CommandRepository commandRepository) {
        this.productRepository = productRepository;
        this.commandRepository = commandRepository;
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

    @Transactional
    public void deleteProductAndCleanUpCommands(Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        // Trouver et nettoyer les commandes associées
        List<Command> commands = this.commandRepository.findAllWithProduct(productId);
        for (Command command : commands) {
            command.getCommandProducts().removeIf(cp -> cp.getProduct().equals(product));
            if (command.getCommandProducts().isEmpty()) {
                this.commandRepository.delete(command);
            } else {
                this.commandRepository.save(command);
            }
        }

        this.productRepository.delete(product);
    }
}