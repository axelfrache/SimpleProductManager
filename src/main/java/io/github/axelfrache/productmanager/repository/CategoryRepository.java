package io.github.axelfrache.productmanager.repository;

import io.github.axelfrache.productmanager.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>{
}
