package io.github.axelfrache.productmanager.repository;

import io.github.axelfrache.productmanager.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}