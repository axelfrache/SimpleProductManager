package io.github.axelfrache.productmanager.repository;

import io.github.axelfrache.productmanager.model.Command;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandRepository extends JpaRepository<Command, Long>{
}
